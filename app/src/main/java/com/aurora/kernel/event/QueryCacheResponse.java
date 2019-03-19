package com.aurora.kernel.event;

import com.aurora.internalservice.internalcache.CachedProcessedFile;

import java.util.List;

/**
 * This event contains the response (processed text) of a QueryCacheRequest.
 */
public class QueryCacheResponse extends Event {
    private List<CachedProcessedFile> mResults;

    public QueryCacheResponse(List<CachedProcessedFile> results) {
        mResults = results;
    }

    public List<CachedProcessedFile> getResults() {
        return mResults;
    }
}
