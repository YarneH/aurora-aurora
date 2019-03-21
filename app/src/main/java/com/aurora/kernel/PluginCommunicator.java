package com.aurora.kernel;

import com.aurora.kernel.event.ListPluginsRequest;
import com.aurora.kernel.event.ListPluginsResponse;
import com.aurora.kernel.event.OpenFileWithPluginRequest;
import com.aurora.kernel.event.PluginProcessorRequest;
import com.aurora.kernel.event.PluginProcessorResponse;
import com.aurora.plugin.BasicPlugin;
import com.aurora.plugin.ProcessedText;
import com.aurora.processingservice.PluginProcessor;

import java.util.List;

import io.reactivex.Observable;

/**
 * Communicator that communicates with Plugin environments
 */
public class PluginCommunicator extends Communicator {
    private PluginRegistry mPluginRegistry;

    private Observable<OpenFileWithPluginRequest> mOpenFileWithPluginRequestObservable;
    private Observable<ListPluginsRequest> mListPluginsRequestObservable;


    public PluginCommunicator(Bus bus, PluginRegistry pluginRegistry) {
        super(bus);

        this.mPluginRegistry = pluginRegistry;

        // Register for requests to open file with plugin
        mOpenFileWithPluginRequestObservable = mBus.register(OpenFileWithPluginRequest.class);

        // When a request comes in, call appropriate function
        //mOpenFileWithPluginRequestObservable.subscribe((OpenFileWithPluginRequest openFileWithPluginRequest) ->
        //         openFileWithPlugin(openFileWithPluginRequest.getPluginName(), openFileWithPluginRequest.getFileRef())
        // );

        // Register for requests to list available plugins
        mListPluginsRequestObservable = mBus.register(ListPluginsRequest.class);

        // When a request comes in, call the appropriate function
        mListPluginsRequestObservable.subscribe((ListPluginsRequest listPluginsRequest) ->
                listPlugins()
        );
    }


    /**
     * Opens a file with a given plugin
     *
     * @param pluginName the name of the plugin to get the settings for
     * @param fileRef    a reference to the file to process
     */
    private void openFileWithPlugin(String fileRef, String pluginName) {
        // TODO
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
