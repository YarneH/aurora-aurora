package com.aurora.kernel.event;

/**
 * Response event that returns whether removing one or multiple files from the cache succeeded.
 *
 * @see RemoveFromCacheRequest
 */
public class RemoveFromCacheResponse implements Event {
    /**
     * Indicates whether the file(s) were removed successfully from the cache
     */
    private boolean mSuccess;

    /**
     * Creates a new RemoveFromCacheResponse
     *
     * @param success indicates whether the file(s) were removed successfully from the cache
     */
    public RemoveFromCacheResponse(boolean success) {
        mSuccess = success;
    }

    /**
     * @return indicates whether the file(s) were removed successfully from the cache
     */
    public boolean isSuccess() {
        return mSuccess;
    }
}
