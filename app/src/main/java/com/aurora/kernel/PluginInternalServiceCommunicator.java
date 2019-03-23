package com.aurora.kernel;

import android.util.Log;

import com.aurora.internalservice.internalprocessor.FileTypeNotSupportedException;
import com.aurora.internalservice.internalprocessor.InternalTextProcessor;
import com.aurora.kernel.event.InternalProcessorRequest;

import java.io.InputStream;

import io.reactivex.Observable;

/**
 * Communicator that communicates with internal services offered to plugin processors
 */
public class PluginInternalServiceCommunicator extends Communicator {

    private Observable<InternalProcessorRequest> internalProcessorEventObservable;
    private InternalTextProcessor internalTextProcessor;

    public PluginInternalServiceCommunicator(Bus mBus) {
        super(mBus);

        internalTextProcessor = new InternalTextProcessor();

        internalProcessorEventObservable = mBus.register(InternalProcessorRequest.class);
        internalProcessorEventObservable.subscribe((InternalProcessorRequest internalProcessorRequest) ->
                processFileWithInternalProcessor(internalProcessorRequest.getFileRef()));
    }

    private void processFileWithInternalProcessor(String fileRef) {
        //TODO Call the internal processor
        Log.d("InternalServiceComm", "Not implemented yet! " + fileRef);
    }

    // TODO remove this temporary test method, should be passed through the Kernel instead of this
    public void processFile(InputStream file, String fileRef) {
        try {
            internalTextProcessor.processFile(file, fileRef);
        } catch (FileTypeNotSupportedException e) {
            e.printStackTrace();
        }
    }
}
