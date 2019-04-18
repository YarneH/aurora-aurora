package com.aurora.kernel.event;

/**
 * Response event to indicate if a file was successfully cached or not
 * @see CacheFileRequest
 */
public class CacheFileResponse extends Event {
    /**
     * A boolean indicating if the file was successfully cached
     */
    private boolean mSuccessful;

    /**
     * Creates a new CacheFileResponse
     *
     * @param successful indicates whether or not caching the file was successful
     */
    public CacheFileResponse(boolean successful) {
        mSuccessful = successful;
    }

    /**
     * @return true if caching was successful, false otherwise
     */
    public boolean isSuccessful() {
        return mSuccessful;
    }
}
