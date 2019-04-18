package com.aurora.kernel;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.aurora.aurora.R;
import com.aurora.auroralib.ExtractedText;
import com.aurora.internalservice.internalcache.CachedProcessedFile;
import com.aurora.kernel.event.InternalProcessorRequest;
import com.aurora.kernel.event.InternalProcessorResponse;
import com.aurora.kernel.event.ListPluginsRequest;
import com.aurora.kernel.event.ListPluginsResponse;
import com.aurora.kernel.event.OpenCachedFileWithPluginRequest;
import com.aurora.kernel.event.OpenFileWithPluginRequest;
import com.aurora.kernel.event.RetrieveFileFromCacheRequest;
import com.aurora.kernel.event.RetrieveFileFromCacheResponse;
import com.aurora.plugin.InternalServices;
import com.aurora.plugin.Plugin;

import java.io.InputStream;
import java.util.List;
import java.util.Set;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;

/**
 * Communicator class that communicates to Aurora app environment
 */
public class AuroraCommunicator extends Communicator {
    /**
     * Tag for logging purposes
     */
    private static final String CLASS_TAG = "AuroraCommunicator";

    /**
     * A reference to the plugin registry
     */
    private PluginRegistry mPluginRegistry;

    /**
     * Creates an AuroraCommunicator. There should be only one AuroraCommunicator at a time
     * @param bus A reference to the unique bus instance over which the communicators will communicate events
     * @param pluginRegistry a reference to the plugin registry
     */
    public AuroraCommunicator(Bus bus, PluginRegistry pluginRegistry) {
        super(bus);
        mPluginRegistry = pluginRegistry;
    }

    /**
     * Open file with a given plugin. This method will first extract
     * the text from the given file reference,
     * then it will send a request to let the plugin make the representation.
     *
     * @param fileRef            a reference to the file that needs to be opened
     * @param fileType           the file type
     * @param file               the input stream of the file
     * @param pluginAction       the target plugin that was selected by the user
     * @param chooser            the chooser intent used for opening the plugin
     * @param applicationContext the android context
     */
    public void openFileWithPlugin(String fileRef, String fileType, InputStream file,
                                   Intent pluginAction, Intent chooser, Context applicationContext) {
        // Create observable to listen to
        Observable<InternalProcessorResponse> internalProcessorResponse =
                mBus.register(InternalProcessorResponse.class);

        // Subscribe to observable
        // The subscribe will only be triggered after the file was processed internally
        internalProcessorResponse
                .map(InternalProcessorResponse::getExtractedText)
                .subscribe((ExtractedText extractedText) ->
                        sendOpenFileRequest(extractedText, pluginAction, chooser, applicationContext));

        // Get internal processing parameters for the plugin from the plugin registry
        String selectedPluginName = getChosenPlugin(pluginAction, applicationContext);
        Plugin selectedPlugin = mPluginRegistry.getPlugin(selectedPluginName);

        // If the plugin exists in the registry, get the set of supported internal services
        if (selectedPlugin != null) {
            Set<InternalServices> internalServices = selectedPlugin.getInternalServices();

            InternalProcessorRequest internalProcessorRequest =
                    new InternalProcessorRequest(fileRef, fileType, file, internalServices);

            // Post request on the bus
            mBus.post(internalProcessorRequest);
        } else {
            Toast.makeText(applicationContext,
                    applicationContext.getString(R.string.plugin_not_in_registry), Toast.LENGTH_LONG).show();
        }

    }


    /**
     * Method to open an already cached file with the plugin
     *
     * @param fileRef          a reference to the file to open
     * @param fileType         the file type of the file to open
     * @param uniquePluginName the name of the plugin that the file was processed with
     * @param context          the android context
     */
    public void openFileWithCache(String fileRef, String fileType, String uniquePluginName, Context context) {
        // Create observable to listen to
        Observable<RetrieveFileFromCacheResponse> retrieveFileFromCacheResponse =
                mBus.register(RetrieveFileFromCacheResponse.class);

        // Subscribe to observable
        retrieveFileFromCacheResponse
                .map(RetrieveFileFromCacheResponse::getProcessedFile)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe((CachedProcessedFile processedFile) -> {
                    if ("{}".equals(processedFile.getJsonRepresentation())) {
                        Toast.makeText(context, context.getString(R.string.cached_file_not_found),
                                Toast.LENGTH_LONG).show();
                        // TODO: change this such that it processes the original file
                    } else {
                        sendOpenCachedFileRequest(processedFile.getJsonRepresentation(), context);
                    }
                });


        // Send request to retrieve file from cache TODO change this (DummyPlugin)!
        RetrieveFileFromCacheRequest request = new RetrieveFileFromCacheRequest(fileRef, uniquePluginName);
        mBus.post(request);
    }

    /**
     * Gets a list of all the available plugins
     *
     * @return a list of basic information on every plugin wrapped in an observable
     */
    public Observable<List<Plugin>> getListOfPlugins() {
        Observable<ListPluginsResponse> mListPluginsResponse
                = this.mBus.register(ListPluginsResponse.class);
        this.mBus.post(new ListPluginsRequest());

        return mListPluginsResponse.map(ListPluginsResponse::getPlugins);
    }

    /**
     * Registers a plugin in the pluginRegistry
     *
     * @param plugin the plugin metadata object
     * @return true if the plugin was successfully saved in the plugin registry, false otherwise
     */
    public boolean registerPlugin(Plugin plugin) {
        return mPluginRegistry.registerPlugin(plugin.getUniqueName(), plugin);
    }

    /**
     * Gets the name of the chosen plugin. This name is to be used for caching and other internal purposes.
     *
     * @param pluginAction An intent to open a file with a plugin
     * @param context      the android context
     * @return the name of the plugin that was selected by the user
     */
    private String getChosenPlugin(Intent pluginAction, Context context) {
        // Get the component name of the selected option
        ComponentName selectedPlugin = pluginAction.resolveActivity(context.getPackageManager());

        return selectedPlugin.getPackageName();
    }

    /**
     * Private handle method to send request to plugin communicator to open file with plugin
     *
     * @param extractedText the extracted text of the file that was internally processed
     * @param pluginAction  the target intent of the chooser
     * @param chooser       the plugin that the user selected
     * @param context       the android context
     */
    private void sendOpenFileRequest(ExtractedText extractedText, Intent pluginAction,
                                     Intent chooser, Context context) {

        // Create request and post it on bus
        OpenFileWithPluginRequest openFileWithPluginRequest =
                new OpenFileWithPluginRequest(extractedText, pluginAction, chooser, context);
        mBus.post(openFileWithPluginRequest);
    }

    /**
     * Private handle method to send request to plugin communicator to open an already cached file with plugin
     *
     * @param jsonRepresentation the representation of the object to represent
     * @param context            the android context
     */
    private void sendOpenCachedFileRequest(String jsonRepresentation, Context context) {
        // Create request and post it on bus

        OpenCachedFileWithPluginRequest openCachedFileWithPluginRequest =
                new OpenCachedFileWithPluginRequest(jsonRepresentation, context);
        mBus.post(openCachedFileWithPluginRequest);
    }
}
