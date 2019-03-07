package com.aurora.kernel;

import android.util.Log;

import com.aurora.kernel.event.OpenFileWithPluginRequest;
import com.aurora.kernel.event.PluginProcessorRequest;
import com.aurora.kernel.event.PluginProcessorResponse;
import com.aurora.kernel.event.PluginSettingsRequest;
import com.aurora.plugin.ProcessedText;
import com.aurora.processingservice.PluginProcessor;

import io.reactivex.Observable;

/**
 * Communicator that communicates with Plugin environments
 */
public class PluginCommunicator extends Communicator {
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

        // Register for requests to show settings
        mPluginSettingsRequestObservable = mBus.register(PluginSettingsRequest.class);

        // When a request comes in, call the appropriate function
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
        Log.d("PluginCommunicator", "Not implemented yet! " + pluginName);

        //PluginSettingsResponse pluginSettingsResponse =
        //new PluginSettingsResponse(mPluginRegistry.resolvePlugin(pluginName).getSettingsActivity());
        //this.mBus.post(pluginSettingsResponse);
    }

    /**
     * @param pluginName the name of the plugin to get the settings for
     * @param fileRef    a reference to the file to process
     */
    private void openFileWithPlugin(String pluginName, String fileRef) {
        // TODO: get file representation
        // TODO: make an OpenFileWithPluginResponse
        Log.d("PluginCommunicator", "Not implemented yet!" + pluginName + " " + fileRef);
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
