package com.aurora.kernel;

import android.util.Log;

import com.aurora.kernel.event.OpenFileWithPluginRequest;
import com.aurora.kernel.event.PluginProcessorRequest;
import com.aurora.kernel.event.PluginProcessorResponse;
import com.aurora.kernel.event.PluginSettingsRequest;
import com.aurora.plugin.PluginFragment;
import com.aurora.plugin.PluginProcessor;
import com.aurora.plugin.ProcessedText;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;

/**
 * Communicator that communicates with Plugin environments
 */
public class PluginCommunicator extends Communicator {
    private PluginFragment mPluginFragment;
    private PluginRegistry mPluginRegistry;

    private Observable<OpenFileWithPluginRequest> mOpenFileWithPluginRequestObservable;
    private Observable<PluginSettingsRequest> mPluginSettingsRequestObservable;

    private Observable<PluginProcessorResponse> mPluginProcessorResponseObservable;

    public PluginCommunicator(Bus bus, PluginRegistry pluginRegistry) {
        super(bus);

        this.mPluginRegistry = pluginRegistry;

        // Register for requests to open file with plugin
        mOpenFileWithPluginRequestObservable = mBus.register(OpenFileWithPluginRequest.class);

        // When a request comes in, call appropriate function
        Disposable openFileWithPluginRequestDisposable =
                mOpenFileWithPluginRequestObservable.subscribe((OpenFileWithPluginRequest openFileWithPluginRequest) ->
                        openFileWithPlugin(openFileWithPluginRequest.getPluginName(), openFileWithPluginRequest.getFileRef())
                );

        // Register for requests to show settings
        mPluginSettingsRequestObservable = mBus.register(PluginSettingsRequest.class);

        // When a request comes in, call the appropriate function
        Disposable pluginSettingsRequestDisposable =
                mPluginSettingsRequestObservable.subscribe((PluginSettingsRequest pluginSettingsRequest) ->
                        getSettingsActivity(pluginSettingsRequest.getPluginName())
                );

        // Subscribe to response events
        mPluginProcessorResponseObservable = mBus.register(PluginProcessorResponse.class);
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
    }


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
        this.mBus.post(new PluginProcessorRequest(pluginProcessor, fileRef));

        return mPluginProcessorResponseObservable.map(PluginProcessorResponse::getProcessedText);
    }


}
