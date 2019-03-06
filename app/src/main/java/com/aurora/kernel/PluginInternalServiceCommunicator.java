package com.aurora.kernel;

import android.util.Log;

import com.aurora.kernel.event.InternalProcessorRequest;

import io.reactivex.Observable;

/**
 * Communicator that communicates with internal services offered to plugin processors
 */
public class PluginInternalServiceCommunicator extends Communicator {

    private Observable<InternalProcessorRequest> internalProcessorEventObservable;

    public PluginInternalServiceCommunicator(Bus mBus) {
        super(mBus);

        internalProcessorEventObservable = mBus.register(InternalProcessorRequest.class);
        internalProcessorEventObservable.subscribe((InternalProcessorRequest internalProcessorRequest) ->
                processFileWithInternalProcessor(internalProcessorRequest.getFileRef()));
    }

    private void processFileWithInternalProcessor(String fileRef) {
        //TODO Call the internal processor
        Log.d("InternalServiceComm", "Not implemented yet! " + fileRef);
    }
}
