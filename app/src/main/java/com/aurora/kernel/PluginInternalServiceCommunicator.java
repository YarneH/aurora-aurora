package com.aurora.kernel;

import android.util.Log;

import com.aurora.auroralib.ExtractedText;
import com.aurora.internalservice.internalprocessor.FileTypeNotSupportedException;
import com.aurora.internalservice.internalprocessor.InternalTextProcessor;
import com.aurora.kernel.event.InternalProcessorRequest;
import com.aurora.kernel.event.InternalProcessorResponse;

import java.io.InputStream;

import io.reactivex.Observable;

/**
 * Communicator that communicates with internal services offered to plugin processors
 */
public class PluginInternalServiceCommunicator extends Communicator {

    /**
     * internal text processor
     */
    private InternalTextProcessor mProcessor;

    /**
     * Observable keeping track of internal processor requests
     */
    private Observable<InternalProcessorRequest> internalProcessorEventObservable;

    public PluginInternalServiceCommunicator(Bus mBus, InternalTextProcessor processor) {
        super(mBus);
        mProcessor = processor;

        internalProcessorEventObservable = mBus.register(InternalProcessorRequest.class);
        internalProcessorEventObservable.subscribe((InternalProcessorRequest internalProcessorRequest) ->
                processFileWithInternalProcessor(internalProcessorRequest.getFile(),
                        internalProcessorRequest.getFileRef(), internalProcessorRequest.getFileType()));
    }

    private void processFileWithInternalProcessor(InputStream file, String fileRef, String type) {
        // Call internal processor
        ExtractedText extractedText = null;
        try {
            extractedText = mProcessor.processFile(file, fileRef, type);
        } catch (FileTypeNotSupportedException e) {
            Log.e("PluginIntSerComm", "File type is not supported!", e);
        }


        // Create response
        InternalProcessorResponse response = new InternalProcessorResponse(extractedText);

        // Post response
        mBus.post(response);
    }

}
