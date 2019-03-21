package com.aurora.kernel;

import com.aurora.auroralib.ExtractedText;
import com.aurora.internalservice.internalprocessor.FileTypeNotSupportedException;
import com.aurora.internalservice.internalprocessor.InternalTextProcessing;
import com.aurora.kernel.event.InternalProcessorRequest;
import com.aurora.kernel.event.InternalProcessorResponse;

import io.reactivex.Observable;

/**
 * Communicator that communicates with internal services offered to plugin processors
 */
public class PluginInternalServiceCommunicator extends Communicator {

    /**
     * internal text processor
     */
    private InternalTextProcessing mProcessing;

    /**
     * Observable keeping track of internal processor requests
     */
    private Observable<InternalProcessorRequest> internalProcessorEventObservable;

    public PluginInternalServiceCommunicator(Bus mBus, InternalTextProcessing processing) {
        super(mBus);
        mProcessing = processing;

        internalProcessorEventObservable = mBus.register(InternalProcessorRequest.class);
        internalProcessorEventObservable.subscribe((InternalProcessorRequest internalProcessorRequest) ->
                processFileWithInternalProcessor(internalProcessorRequest.getFileRef()));
    }

    private void processFileWithInternalProcessor(String fileRef) {
        // Call internal processor
        ExtractedText extractedText = null;
        try {
            extractedText = mProcessing.processFile(fileRef);
        } catch (FileTypeNotSupportedException e) {
            e.printStackTrace();
        }

        // Create response
        InternalProcessorResponse response = new InternalProcessorResponse(extractedText);

        // Post response
        mBus.post(response);
    }
}
