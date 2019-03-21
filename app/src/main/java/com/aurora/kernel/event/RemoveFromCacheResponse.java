package com.aurora.kernel.event;

/**
 * Response event that returns whether removing one or multiple files from the cache succeeded.
 */
public class RemoveFromCacheResponse extends Event {
    private boolean mSuccess;

    public RemoveFromCacheResponse(boolean success) {
        mSuccess = success;
    }

    public boolean isSuccess() {
        return mSuccess;
    }
}
