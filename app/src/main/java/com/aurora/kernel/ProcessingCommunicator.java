package com.aurora.kernel;

import android.util.Log;

import com.aurora.kernel.event.InternalProcessorEvent;
import com.aurora.kernel.event.PluginProcessorEvent;
import com.aurora.plugin.PluginProcessor;

import java.util.Map;

import io.reactivex.Observable;

/**
 * Communicator that communicates with Plugin processors
 */
public class ProcessingCommunicator extends Communicator {

    private PluginProcessor activePluginProcessor;

    private Observable<PluginProcessorEvent> pluginProcessorEventObservable;

    public ProcessingCommunicator(Bus mBus) {
        super(mBus);

        pluginProcessorEventObservable = mBus.register(PluginProcessorEvent.class);
        pluginProcessorEventObservable.subscribe((PluginProcessorEvent pluginProcessorEvent) -> {
            processFileWithPluginProcessor(pluginProcessorEvent.getPluginProcessor(), pluginProcessorEvent.getFileRef());
        });
    }

    private void processFileWithPluginProcessor(PluginProcessor pluginProcessor, String fileref) {

        Log.d("ProcessorCommunicator", "Not implemented yet!");
    }

    /**
     * Part of API exposed to plugin processors to process a file with the internal processor of Aurora
     */
    public void processFileWithAuroraProcessor(String fileref, Map<String, ?> parameters) {
        this.mBus.post(new InternalProcessorEvent(fileref, parameters));

        Log.d("ProcessorCommunicator", "Not implemented yet!");
    }
}
