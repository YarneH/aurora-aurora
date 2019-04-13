package com.aurora.kernel;

import android.util.Log;

import com.aurora.auroralib.ExtractedText;
import com.aurora.internalservice.internalprocessor.FileTypeNotSupportedException;
import com.aurora.internalservice.internalprocessor.InternalTextProcessor;
import com.aurora.kernel.event.InternalProcessorRequest;
import com.aurora.kernel.event.InternalProcessorResponse;
import com.aurora.plugin.InternalServices;

import java.io.InputStream;
import java.util.Set;

import io.reactivex.Observable;

/**
 * Communicator that communicates with internal services offered to plugin processors
 */
public class PluginInternalServiceCommunicator extends Communicator {

    /**
     * internal text processor
     */
    private InternalTextProcessor mInternalTextProcessor;

    /**
     * Observable keeping track of internal processor requests
     */
    private Observable<InternalProcessorRequest> mInternalProcessorRequestObservable;

    public PluginInternalServiceCommunicator(Bus mBus, InternalTextProcessor processor) {
        super(mBus);
        mInternalTextProcessor = processor;

        mInternalProcessorRequestObservable = mBus.register(InternalProcessorRequest.class);
        mInternalProcessorRequestObservable.subscribe((InternalProcessorRequest request) ->
                processFileWithInternalProcessor(request.getFile(),
                        request.getFileRef(), request.getFileType(), request.getInternalServices()));
    }

    private void processFileWithInternalProcessor(InputStream file, String fileRef, String type,
                                                  Set<InternalServices> internalServices) {

        ExtractedText extractedText = null;

        // Perform internal services that are in the given set
        if (internalServices.contains(InternalServices.TEXT_EXTRACTION)) {
            // Call internal text processor
            try {
                extractedText = mInternalTextProcessor.processFile(file, fileRef, type);
            } catch (FileTypeNotSupportedException e) {
                Log.e("PluginIntSerComm", "File type is not supported!", e);
            }
        }


        // Create response
        InternalProcessorResponse response = new InternalProcessorResponse(extractedText);

        // Post response
        mBus.post(response);
    }

}
