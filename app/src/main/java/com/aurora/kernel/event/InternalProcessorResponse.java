package com.aurora.kernel.event;

import com.aurora.auroralib.ExtractedText;

/**
 * Event to respond that a file has been processed internally
 */
public class InternalProcessorResponse extends Event {
    private ExtractedText mExtractedText;

    public InternalProcessorResponse(ExtractedText extractedText) {
        mExtractedText = extractedText;
    }

    public ExtractedText getExtractedText() {
        return mExtractedText;
    }
}
