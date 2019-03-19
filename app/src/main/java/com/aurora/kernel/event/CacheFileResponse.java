package com.aurora.kernel.event;

/**
 * Response event to indicate if a file was successfully cached or not
 */
public class CacheFileResponse extends Event {
    private boolean mSuccessful;

    public CacheFileResponse(boolean successful) {
        mSuccessful = successful;
    }

    public boolean isSuccessful() {
        return mSuccessful;
    }
}
