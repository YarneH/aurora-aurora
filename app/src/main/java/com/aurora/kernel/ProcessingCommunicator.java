package com.aurora.kernel;

import com.aurora.internalservice.internalprocessor.ExtractedText;
import com.aurora.kernel.event.InternalProcessorRequest;
import com.aurora.kernel.event.InternalProcessorResponse;
import com.aurora.kernel.event.PluginProcessorRequest;
import com.aurora.kernel.event.PluginProcessorResponse;
import com.aurora.processingservice.PluginProcessor;

import io.reactivex.Observable;

/**
 * Communicator that communicates with Plugin processors
 */
public class ProcessingCommunicator extends Communicator {

    private PluginProcessor activePluginProcessor;

    private Observable<PluginProcessorRequest> mPluginProcessorRequestObservable;

    public ProcessingCommunicator(Bus mBus) {
        super(mBus);

        // Register for incoming pluginProcessor requests
        mPluginProcessorRequestObservable = mBus.register(PluginProcessorRequest.class);

        // When requests come in, call function
        mPluginProcessorRequestObservable.subscribe((PluginProcessorRequest pluginProcessorRequest) ->
                processFileWithPluginProcessor(pluginProcessorRequest.getPluginProcessor(),
                        pluginProcessorRequest.getFileRef()));
    }

    /**
     * Helper method that processes file with a given pluginprocessor
     *
     * @param pluginProcessor the processor to process the file with
     * @param fileRef         a reference to where the file can be found
     */
    private void processFileWithPluginProcessor(PluginProcessor pluginProcessor, String fileRef) {
        PluginProcessorResponse pluginProcessorResponse =
                new PluginProcessorResponse(pluginProcessor.processFileWithPluginProcessor(fileRef));
        this.mBus.post(pluginProcessorResponse);
    }

    /**
     * Processes a file internally given a file ref
     *
     * @param fileRef a reference to where the file can be found
     * @return An observable that contains the processed text
     */
    public Observable<ExtractedText> processFileWithAuroraProcessor(String fileRef) {
        Observable<InternalProcessorResponse> mInternalProcessorResponseObservable
                = mBus.register(InternalProcessorResponse.class);
        this.mBus.post(new InternalProcessorRequest(fileRef));

        return mInternalProcessorResponseObservable.map(InternalProcessorResponse::getExtractedText);
    }
}
