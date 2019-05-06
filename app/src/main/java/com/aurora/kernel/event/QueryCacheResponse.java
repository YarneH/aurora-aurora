package com.aurora.kernel.event;

import android.support.annotation.NonNull;

import com.aurora.internalservice.internalcache.CachedFileInfo;

import java.util.List;

/**
 * This event contains the response (processed text) of a QueryCacheRequest.
 *
 * @see QueryCacheRequest
 */
public class QueryCacheResponse implements Event {
    /**
     * A list of info objects about the cached files
     */
    private List<CachedFileInfo> mResults;

    /**
     * Creates a new QueryCacheResponse
     *
     * @param results a list of info objects about the cached files
     */
    public QueryCacheResponse(@NonNull List<CachedFileInfo> results) {
        mResults = results;
    }

    /**
     * @return a list of info objects about the cached files
     */
    public List<CachedFileInfo> getResults() {
        return mResults;
    }
}
