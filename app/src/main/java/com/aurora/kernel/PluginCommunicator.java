package com.aurora.kernel;

import android.app.Activity;
import android.support.v4.app.Fragment;

import com.aurora.aurora.NotFoundActivity;
import com.aurora.aurora.NotFoundFragment;
import com.aurora.externalservice.PluginEnvironment;
import com.aurora.kernel.event.InternalProcessorRequest;
import com.aurora.kernel.event.ListPluginsResponse;
import com.aurora.kernel.event.ListPluginsRequest;
import com.aurora.kernel.event.OpenFileWithPluginRequest;
import com.aurora.kernel.event.OpenFileWithPluginResponse;
import com.aurora.kernel.event.PluginProcessorRequest;
import com.aurora.kernel.event.PluginProcessorResponse;
import com.aurora.kernel.event.PluginSettingsRequest;
import com.aurora.kernel.event.PluginSettingsResponse;
import com.aurora.plugin.BasicPlugin;
import com.aurora.plugin.ProcessedText;
import com.aurora.processingservice.PluginProcessor;

import java.io.InputStream;
import java.util.List;

import io.reactivex.Observable;

/**
 * Communicator that communicates with Plugin environments
 */
public class PluginCommunicator extends Communicator {
    private PluginRegistry mPluginRegistry;

    private Observable<OpenFileWithPluginRequest> mOpenFileWithPluginRequestObservable;
    private Observable<PluginSettingsRequest> mPluginSettingsRequestObservable;
    private Observable<ListPluginsRequest> mListPluginsRequestObservable;


    public PluginCommunicator(Bus bus, PluginRegistry pluginRegistry) {
        super(bus);

        this.mPluginRegistry = pluginRegistry;

        // Register for requests to open file with plugin
        mOpenFileWithPluginRequestObservable = mBus.register(OpenFileWithPluginRequest.class);

        // When a request comes in, call appropriate function
        mOpenFileWithPluginRequestObservable.subscribe((OpenFileWithPluginRequest openFileWithPluginRequest) ->
                openFileWithPlugin(openFileWithPluginRequest.getPluginName(),
                        openFileWithPluginRequest.getFile() ,openFileWithPluginRequest.getFileRef())
        );

        // Register for requests to show settings
        mPluginSettingsRequestObservable = mBus.register(PluginSettingsRequest.class);

        // When a request comes in, call the appropriate function
        mPluginSettingsRequestObservable.subscribe((PluginSettingsRequest pluginSettingsRequest) ->
                getSettingsActivity(pluginSettingsRequest.getPluginName())
        );

        // Register for requests to list available plugins
        mListPluginsRequestObservable = mBus.register(ListPluginsRequest.class);

        // When a request comes in, call the appropriate function
        mListPluginsRequestObservable.subscribe((ListPluginsRequest listPluginsRequest) ->
                listPlugins()
        );
    }

    /**
     * Requests the settings of a given plugin in the plugin registry
     *
     * @param pluginName the name of the plugin to get the settings for
     */
    private void getSettingsActivity(String pluginName) {
        // Load the plugin
        PluginEnvironment plugin = mPluginRegistry.loadPlugin(pluginName);

        // Get the settings from the plugin
        Class<? extends Activity> settingsActivity;

        if (plugin != null) {
            settingsActivity = plugin.getSettingsActivity();
        } else {
            // Create not found activity
            settingsActivity = NotFoundActivity.class;
        }

        // Create a response and post it, response will contain null if plugin was not found
        PluginSettingsResponse pluginSettingsResponse = new PluginSettingsResponse(settingsActivity);

        mBus.post(pluginSettingsResponse);
    }

    /**
     * Opens a file with a given plugin
     *
     * @param pluginName the name of the plugin to get the settings for
     * @param fileRef    a reference to the file to process
     */
    private void openFileWithPlugin(String pluginName, InputStream file, String fileRef) {
        PluginEnvironment plugin =  mPluginRegistry.loadPlugin(pluginName);

        /*TODO: Unclear where InternalProcessing will be called from in the reworked Kernel.
        * Put it here because the plugin doesn't need to be known.
        * */
        // TODO This will return an event, handle this
        this.mBus.post(new InternalProcessorRequest(file, fileRef));

        Fragment pluginFragment;

        if (plugin != null) {
            pluginFragment = plugin.openFile(fileRef);
        } else {
            // Create not found fragment
            pluginFragment = new NotFoundFragment();
        }

        // Create a response and post it, response will contain null if plugin was not found
        OpenFileWithPluginResponse pluginResponse = new OpenFileWithPluginResponse(pluginFragment);

        mBus.post(pluginResponse);
    }

    /**
     * Lists all available plugins. It actually fires a ListPluginsResponseEvent that should be
     * subscribed on by the AuroraCommunicator
     */
    private void listPlugins() {
        // Get available plugins from plugin registry
        List<BasicPlugin> pluginList = mPluginRegistry.getPlugins();

        // Make a response event and post it
        ListPluginsResponse response = new ListPluginsResponse(pluginList);
        mBus.post(response);
    }


    /**
     * Delegates work to a certain pluginProcessor and returns processed text when ready
     *
     * @param pluginProcessor the pluginProcessor to process the file with
     * @param fileRef         a reference to the file to process
     * @return an observable containing the processed text
     */
    public Observable<ProcessedText> processFileWithPluginProcessor(PluginProcessor pluginProcessor, String fileRef) {
        Observable<PluginProcessorResponse> mPluginProcessorResponseObservable
                = mBus.register(PluginProcessorResponse.class);
        this.mBus.post(new PluginProcessorRequest(pluginProcessor, fileRef));

        return mPluginProcessorResponseObservable.map(PluginProcessorResponse::getProcessedText);
    }


}
