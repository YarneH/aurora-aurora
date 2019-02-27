package com.aurora.kernel.event;


import com.aurora.plugin.ProcessedText;

/**
 * Event to respond with a processed file that was processed by the PluginProcessor
 */
public class PluginProcessorResponse extends Event {
    private ProcessedText mProcessedText;

    public PluginProcessorResponse(ProcessedText processedText) {
        mProcessedText = processedText;
    }

    public ProcessedText getProcessedText() {
        return mProcessedText;
    }
}
