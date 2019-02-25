package com.aurora.kernel;

import android.util.Log;

import com.aurora.kernel.event.InternalProcessorEvent;

import java.util.Map;

import io.reactivex.Observable;

/**
 * Communicator that communicates with internal services offered to plugin processors
 */
public class PluginInternalServiceCommunicator extends Communicator {

    private Observable<InternalProcessorEvent> internalProcessorEventObservable;

    public PluginInternalServiceCommunicator(Bus mBus) {
        super(mBus);

        internalProcessorEventObservable = mBus.register(InternalProcessorEvent.class);
        internalProcessorEventObservable.subscribe((InternalProcessorEvent internalProcessorEvent) -> {
            processFileWithInternalProcessor(internalProcessorEvent.getFileRef(), internalProcessorEvent.getParameters());
        });
    }

    private void processFileWithInternalProcessor(String fileRef, Map<String, ?> parameters) {
        Log.d("InternalServiceComm", "Not implemented yet!");
    }
}
