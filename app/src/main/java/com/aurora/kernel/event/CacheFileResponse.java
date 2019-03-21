package com.aurora.kernel.event;

/**
 * Response event to indicate if a file was successfully cached or not
 */
public class CacheFileResponse extends Event {
    /**
     * A boolean indicating if the file was successfully cached
     */
    private boolean mSuccessful;

    public CacheFileResponse(boolean successful) {
        mSuccessful = successful;
    }

    public boolean isSuccessful() {
        return mSuccessful;
    }
}
