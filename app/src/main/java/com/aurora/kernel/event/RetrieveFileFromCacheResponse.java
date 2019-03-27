package com.aurora.kernel.event;

import com.aurora.internalservice.internalcache.CachedProcessedFile;

/**
 * Response event that contains the retrieved file from the cache.
 */
public class RetrieveFileFromCacheResponse extends Event {
    /**
     * The processed file representation
     */
    private CachedProcessedFile mProcessedFile;

    public RetrieveFileFromCacheResponse(CachedProcessedFile processedFile) {
        mProcessedFile = processedFile;
    }

    public CachedProcessedFile getProcessedFile() {
        return mProcessedFile;
    }
}
