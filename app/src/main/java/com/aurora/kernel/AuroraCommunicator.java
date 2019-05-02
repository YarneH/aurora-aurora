package com.aurora.kernel;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.aurora.aurora.R;
import com.aurora.auroralib.ExtractedText;
import com.aurora.internalservice.internalcache.CachedFileInfo;
import com.aurora.internalservice.internalcache.CachedProcessedFile;
import com.aurora.kernel.event.InternalProcessorRequest;
import com.aurora.kernel.event.InternalProcessorResponse;
import com.aurora.kernel.event.ListPluginsRequest;
import com.aurora.kernel.event.ListPluginsResponse;
import com.aurora.kernel.event.OpenCachedFileWithPluginRequest;
import com.aurora.kernel.event.OpenFileWithPluginChooserRequest;
import com.aurora.kernel.event.OpenFileWithPluginRequest;
import com.aurora.kernel.event.QueryCacheRequest;
import com.aurora.kernel.event.QueryCacheResponse;
import com.aurora.kernel.event.RetrieveFileFromCacheRequest;
import com.aurora.kernel.event.RetrieveFileFromCacheResponse;
import com.aurora.plugin.InternalServices;
import com.aurora.plugin.Plugin;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Observer;
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
     *
     * @param bus            A reference to the unique bus instance over which the communicators will communicate events
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
     * @param uniquePluginName   the (unique) name of the plugin to open the file with. This should be obtained from
     *                           the own chooser.
     * @param applicationContext the android context
     */
    public void openFileWithPlugin(String fileRef, String fileType, InputStream file,
                                   String uniquePluginName, Context applicationContext) {

        // Register observable
        Observable<InternalProcessorResponse> internalProcessorResponseObservable =
                mBus.register(InternalProcessorResponse.class);

        // Subscribe to observable
        // The subscribe will only be triggered after the file was processed internally
        internalProcessorResponseObservable
                .map(InternalProcessorResponse::getExtractedText)
                .take(1)
                .subscribe((ExtractedText extractedText) ->
                                sendOpenFileRequest(extractedText, uniquePluginName, applicationContext)
                        , (Throwable e) ->
                                Log.e(CLASS_TAG,
                                        "Something went wrong when receiving the internally processed file.", e)
                );


        // TODO: this is bypass code. As soon as plugins are registered in the registry, this should be removed
        List<InternalServices> internalServices =
                new ArrayList<>(Arrays.asList(
                        InternalServices.TEXT_EXTRACTION,
                        InternalServices.IMAGE_EXTRACTION,
                        InternalServices.NLP_TOKENIZE,
                        InternalServices.NLP_SSPLIT,
                        InternalServices.NLP_POS
                ));
        InternalProcessorRequest internalProcessorRequest =
                new InternalProcessorRequest(fileRef, fileType, file, internalServices);

        // Post request on the bus
        mBus.post(internalProcessorRequest);
    }

    //TODO: delete once custom pluginPicker is finished

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
    public void openFileWithPluginChooser(String fileRef, String fileType, InputStream file,
                                          Intent pluginAction, Intent chooser, Context applicationContext) {

        // Register observable
        Observable<InternalProcessorResponse> internalProcessorResponseObservable =
                mBus.register(InternalProcessorResponse.class);

        // Subscribe to observable
        // The subscribe will only be triggered after the file was processed internally
        internalProcessorResponseObservable
                .map(InternalProcessorResponse::getExtractedText)
                .take(1)
                .subscribe((ExtractedText extractedText) ->
                                sendOpenFileChooserRequest(extractedText, pluginAction, chooser, applicationContext)
                        , (Throwable e) ->
                                Log.e(CLASS_TAG,
                                        "Something went wrong when receiving the internally processed file.", e)
                );


        // TODO: this is bypass code. As soon as plugins are registered in the registry, this should be removed
        List<InternalServices> internalServices =
                new ArrayList<>(Arrays.asList(
                        InternalServices.TEXT_EXTRACTION,
                        InternalServices.IMAGE_EXTRACTION,
                        InternalServices.NLP_TOKENIZE,
                        InternalServices.NLP_SSPLIT,
                        InternalServices.NLP_POS
                ));
        InternalProcessorRequest internalProcessorRequest =
                new InternalProcessorRequest(fileRef, fileType, file, internalServices);

        // Post request on the bus
        mBus.post(internalProcessorRequest);
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
                .take(1)
                .subscribe((CachedProcessedFile processedFile) -> {
                            if ("{}".equals(processedFile.getJsonRepresentation())) {
                                Toast.makeText(context,
                                        context.getString(R.string.cached_file_not_found_processing_file),
                                        Toast.LENGTH_LONG).show();
                                // TODO: change this such that it processes the original file

                            } else {
                                sendOpenCachedFileRequest(processedFile.getJsonRepresentation(),
                                        processedFile.getUniquePluginName(), context);
                            }
                        }, (Throwable e) ->
                                Log.e(CLASS_TAG, "Something went wrong while retrieving a file from the cache!", e)
                );


        // Send request to retrieve file from cache TODO change this (DummyPlugin)!
        RetrieveFileFromCacheRequest request = new RetrieveFileFromCacheRequest(fileRef, uniquePluginName);
        mBus.post(request);
    }

    /**
     * Gets a list of all cached files, ordered on date in ascending order
     *
     * @param maxLength The maximum number of entries that should be returned, or <= 0 if you want all files
     * @param observer  an observer instance containing code that will be executed when the result comes in
     */
    public void getListOfCachedFiles(int maxLength,
                                     Observer<List<CachedFileInfo>> observer) {
        // Create observable to listen to
        Observable<QueryCacheResponse> queryCacheResponseObservable = mBus.register(QueryCacheResponse.class);

        queryCacheResponseObservable
                .map(QueryCacheResponse::getResults)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);

        // Create request and post on bus
        QueryCacheRequest request = new QueryCacheRequest(maxLength);
        mBus.post(request);

    }

    /**
     * Gets a list of all the available plugins
     *
     * @param observer an observer containing code that will be executed when the list of plugins comes in
     */
    public void getListOfPlugins(Observer<List<Plugin>> observer) {
        Observable<ListPluginsResponse> mListPluginsResponse
                = this.mBus.register(ListPluginsResponse.class);

        mListPluginsResponse
                .map(ListPluginsResponse::getPlugins)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);

        this.mBus.post(new ListPluginsRequest());
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
     * Private handle method to send request to plugin communicator to open file with plugin
     *
     * @param extractedText    the extracted text of the file that was internally processed
     * @param uniquePluginName the (unique) name of the plugin to open the file with
     * @param context          the android context
     */
    private void sendOpenFileRequest(ExtractedText extractedText, String uniquePluginName, Context context) {

        // Create request and post it on bus
        OpenFileWithPluginRequest openFileWithPluginRequest =
                new OpenFileWithPluginRequest(extractedText, uniquePluginName, context);
        mBus.post(openFileWithPluginRequest);
    }

    //TODO delete when there is a custom picker

    /**
     * Private handle method to send request to plugin communicator to open file with plugin
     *
     * @param extractedText the extracted text of the file that was internally processed
     * @param pluginAction  the target intent of the chooser
     * @param chooser       the plugin that the user selected
     * @param context       the android context
     */
    private void sendOpenFileChooserRequest(ExtractedText extractedText, Intent pluginAction,
                                            Intent chooser, Context context) {

        // Create request and post it on bus
        OpenFileWithPluginChooserRequest openFileWithPluginChooserRequest =
                new OpenFileWithPluginChooserRequest(extractedText, pluginAction, chooser, context);
        mBus.post(openFileWithPluginChooserRequest);
    }

    /**
     * Private handle method to send request to plugin communicator to open an already cached file with plugin
     *
     * @param jsonRepresentation the representation of the object to represent
     * @param uniquePluginName   the name of the plugin that the file was processed with
     * @param context            the android context
     */
    private void sendOpenCachedFileRequest(String jsonRepresentation, String uniquePluginName, Context context) {
        // Create request and post it on bus

        OpenCachedFileWithPluginRequest openCachedFileWithPluginRequest =
                new OpenCachedFileWithPluginRequest(jsonRepresentation, uniquePluginName, context);
        mBus.post(openCachedFileWithPluginRequest);
    }
}
