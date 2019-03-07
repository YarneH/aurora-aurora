package com.aurora.kernel;

import android.app.Activity;
import android.support.v4.app.Fragment;

import com.aurora.aurora.NotFoundActivity;
import com.aurora.aurora.NotFoundFragment;
import com.aurora.externalservice.PluginEnvironment;
import com.aurora.kernel.event.OpenFileWithPluginRequest;
import com.aurora.kernel.event.OpenFileWithPluginResponse;
import com.aurora.kernel.event.PluginProcessorRequest;
import com.aurora.kernel.event.PluginProcessorResponse;
import com.aurora.kernel.event.PluginSettingsRequest;
import com.aurora.kernel.event.PluginSettingsResponse;
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
     * Requests the settings of a given plugin in the plugin registry
     *
     * @param pluginName the name of the plugin to get the settings for
     */
    private void getSettingsActivity(String pluginName) {
        // Load the plugin
        PluginEnvironment plugin = mPluginRegistry.loadPlugin(pluginName);

        // Get the settings from the plugin
        Class<? extends Activity> settingsActivity = null;

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
     * @param pluginName the name of the plugin to get the settings for
     * @param fileRef    a reference to the file to process
     */
    private void openFileWithPlugin(String pluginName, String fileRef) {
        PluginEnvironment plugin =  mPluginRegistry.loadPlugin(pluginName);

        Fragment pluginFragment = null;

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
