package com.aurora.kernel.event;

import com.aurora.auroralib.ExtractedText;

/**
 * Event to respond that a file has been processed internally
 * @see InternalProcessorRequest
 */
public class InternalProcessorResponse extends Event {
    /**
     * The text extracted by the internal processing
     */
    private ExtractedText mExtractedText;

    /**
     * Creates a new InternalProcessorResponse
     *
     * @param extractedText the text extracted by the internal processing
     */
    public InternalProcessorResponse(ExtractedText extractedText) {
        mExtractedText = extractedText;
    }

    /**
     * @return The text extracted by the internal processing
     */
    public ExtractedText getExtractedText() {
        return mExtractedText;
    }
}
