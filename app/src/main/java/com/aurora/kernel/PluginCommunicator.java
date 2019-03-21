package com.aurora.kernel;

import android.util.Log;

import com.aurora.auroralib.ExtractedText;
import com.aurora.kernel.event.ListPluginsRequest;
import com.aurora.kernel.event.ListPluginsResponse;
import com.aurora.kernel.event.OpenFileWithPluginRequest;
import com.aurora.plugin.BasicPlugin;

import java.util.List;

import io.reactivex.Observable;

/**
 * Communicator that communicates with Plugin environments
 */
public class PluginCommunicator extends Communicator {
    private static final String CLASS_TAG = "PluginCommunicator";

    private PluginRegistry mPluginRegistry;

    private Observable<OpenFileWithPluginRequest> mOpenFileWithPluginRequestObservable;
    private Observable<ListPluginsRequest> mListPluginsRequestObservable;


    public PluginCommunicator(Bus bus, PluginRegistry pluginRegistry) {
        super(bus);

        this.mPluginRegistry = pluginRegistry;

        // Register for requests to open file with plugin
        mOpenFileWithPluginRequestObservable = mBus.register(OpenFileWithPluginRequest.class);

        // When a request comes in, call appropriate function
        mOpenFileWithPluginRequestObservable.subscribe((OpenFileWithPluginRequest openFileWithPluginRequest) ->
                openFileWithPlugin(openFileWithPluginRequest.getExtractedText(), openFileWithPluginRequest.getPluginName())
        );

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
     * @param extractedText the extracted text of the file to open
     * @param pluginName    the name of the plugin to open the file with
     *                      TODO: change to other type when Android picker is used
     * TODO: add tests for this method
     */
    private void openFileWithPlugin(ExtractedText extractedText, String pluginName) {
        // TODO: fire intent to given plugin containing the extracted text
        Log.d(CLASS_TAG, "Not implemented yet " + extractedText + " " + pluginName);
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
}
