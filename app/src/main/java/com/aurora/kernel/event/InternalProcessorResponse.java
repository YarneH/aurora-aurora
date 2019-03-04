package com.aurora.kernel.event;

import com.aurora.plugin.ProcessedText;

/**
 * Event to respond that a file has been processed internally
 */
public class InternalProcessorResponse extends Event {
    private ProcessedText mProcessedText;

    public InternalProcessorResponse(ProcessedText processedText) {
        mProcessedText = processedText;
    }

    public ProcessedText getProcessedText() {
        return mProcessedText;
    }
}
