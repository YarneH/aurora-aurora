package com.aurora.kernel;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.aurora.kernel.event.OpenFileWithPluginRequest;
import com.aurora.kernel.event.PluginProcessorRequest;
import com.aurora.kernel.event.PluginSettingsRequest;
import com.aurora.plugin.PluginFragment;
import com.aurora.plugin.PluginProcessor;

import io.reactivex.Observable;

/**
 * Communicator that communicates with Plugin environments
 */
public class PluginCommunicator extends Communicator {
    private PluginFragment mPluginFragment;
    private PluginRegistry mPluginRegistry;

    private Observable<OpenFileWithPluginRequest> mOpenFileWithPluginRequestObservable;
    private Observable<PluginSettingsRequest> mPluginSettingsRequestObservable;

    public PluginCommunicator(Bus bus, PluginRegistry pluginRegistry) {
        super(bus);

        this.mPluginRegistry = pluginRegistry;

        // Register for requests to open file with plugin
        mOpenFileWithPluginRequestObservable = mBus.register(OpenFileWithPluginRequest.class);

        // When a request comes in, call appropriate function
        mOpenFileWithPluginRequestObservable.subscribe((OpenFileWithPluginRequest openFileWithPluginRequest) ->
                openFileWithPlugin(openFileWithPluginRequest.getPluginName(), openFileWithPluginRequest.getFileRef())
        );


        mPluginSettingsRequestObservable = mBus.register(PluginSettingsRequest.class);
        mPluginSettingsRequestObservable.subscribe((PluginSettingsRequest pluginSettingsRequest) ->
                getSettingsActivity(pluginSettingsRequest.getPluginName())
        );

    }

    /**
     * Requests the settings of a given plugin
     *
     * @param pluginName the name of the plugin to get the settings for
     */
    private void getSettingsActivity(String pluginName) {
        // TODO: get settings
        // TODO: make a PluginSettingsResponse
        Log.d("PluginCommunicator", "Not implemented yet!");
    }


    private void openFileWithPlugin(String pluginName, String fileRef) {
        // TODO: get file representation
        // TODO: make an OpenFileWithPluginResponse
        Log.d("PluginCommunicator", "Not implemented yet!");
    }

    public void processFileWithPluginProcessor(PluginProcessor pluginProcessor, String fileRef) {

        // TODO This event should be captured by processingcomm and internalcache, but only be replied once
        // TODO Make different request and handle the results here
        this.mBus.post(new PluginProcessorRequest(pluginProcessor, fileRef));

        Log.d("PluginCommunicator", "Not implemented yet!");
    }


}
