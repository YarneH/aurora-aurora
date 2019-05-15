package com.aurora.kernel.event;

/**
 * This event is sent to show the user something went wrong
 */
public class DocumentNotSupportedEvent implements Event {
    /**
     * The reason why the document could not be processed
     */
    private String mReason;

    public DocumentNotSupportedEvent(String reason) {
        mReason = reason;
    }

    /**
     * @return the reason why the document could not be processed
     */
    public String getReason() {
        return mReason;
    }
}
