package com.aurora.kernel;

import android.content.Context;
import android.content.Intent;

import com.aurora.auroralib.ExtractedText;
import com.aurora.kernel.event.InternalProcessorRequest;
import com.aurora.kernel.event.InternalProcessorResponse;
import com.aurora.kernel.event.ListPluginsRequest;
import com.aurora.kernel.event.ListPluginsResponse;
import com.aurora.kernel.event.OpenFileWithPluginRequest;
import com.aurora.plugin.BasicPlugin;

import java.util.List;

import io.reactivex.Observable;

/**
 * Communicator class that communicates to Aurora app environment
 */
public class AuroraCommunicator extends Communicator {
    private static final String CLASS_TAG = "AuroraCommunicator";

    public AuroraCommunicator(Bus mBus) {
        super(mBus);

    }

    /**
     * Open file with a given plugin. This method will first extract the text from the given file reference,
     * then it will send a request to let the plugin make the representation.
     *
     * @param fileRef      a reference to the file that needs to be opened
     * @param targetPlugin the plugin to open the file with
     * @param context      the android context
     */
    public void openFileWithPlugin(String fileRef, Intent targetPlugin, Context context) {
        // Create observable to listen to
        Observable<InternalProcessorResponse> internalProcessorResponse =
                mBus.register(InternalProcessorResponse.class);

        // Subscribe to observable
        // The subscribe will only be triggered after the file was processed internally
        internalProcessorResponse
                .map(InternalProcessorResponse::getExtractedText)
                .subscribe((ExtractedText extractedText) ->
                        sendOpenFileRequest(extractedText, targetPlugin, context));

        // First create internal processing
        InternalProcessorRequest internalProcessorRequest = new InternalProcessorRequest(fileRef);

        // Post request on the bus
        mBus.post(internalProcessorRequest);
    }

    /**
     * Gets a list of all the available plugins
     *
     * @return a list of basic information on every plugin wrapped in an observable
     */
    public Observable<List<BasicPlugin>> getListOfPlugins() {
        Observable<ListPluginsResponse> mListPluginsResponse
                = this.mBus.register(ListPluginsResponse.class);
        this.mBus.post(new ListPluginsRequest());

        return mListPluginsResponse.map(ListPluginsResponse::getPlugins);
    }

    /**
     * Private handle method to send request to plugin communicator to open file with plugin
     *
     * @param extractedText the extracted text of the file that was internally processed
     * @param targetPlugin  the plugin to open the file with
     * @param context       the android context
     */
    private void sendOpenFileRequest(ExtractedText extractedText, Intent targetPlugin, Context context) {
        // Create request and post it on bus
        OpenFileWithPluginRequest openFileWithPluginRequest = new OpenFileWithPluginRequest(extractedText, targetPlugin, context);
        mBus.post(openFileWithPluginRequest);
    }

}
