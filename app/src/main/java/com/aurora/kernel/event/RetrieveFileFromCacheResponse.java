package com.aurora.kernel.event;

import com.aurora.internalservice.internalcache.CachedProcessedFile;

/**
 * Response event that contains the retrieved file from the cache.
 *
 * @see RetrieveFileFromCacheRequest
 */
public class RetrieveFileFromCacheResponse extends Event {
    /**
     * The processed file representation
     */
    private CachedProcessedFile mProcessedFile;

    /**
     * Creates a new RetrieveFileFromCacheResponse
     *
     * @param processedFile the retrieved processed file representation
     */
    public RetrieveFileFromCacheResponse(CachedProcessedFile processedFile) {
        mProcessedFile = processedFile;
    }

    /**
     * @return the retrieved processed file representation
     */
    public CachedProcessedFile getProcessedFile() {
        return mProcessedFile;
    }
}
