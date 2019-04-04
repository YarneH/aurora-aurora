package com.aurora.kernel.event;

import com.aurora.internalservice.internalcache.CachedFileInfo;

import java.util.List;

/**
 * This event contains the response (processed text) of a QueryCacheRequest.
 */
public class QueryCacheResponse extends Event {
    private List<CachedFileInfo> mResults;

    public QueryCacheResponse(List<CachedFileInfo> results) {
        mResults = results;
    }

    public List<CachedFileInfo> getResults() {
        return mResults;
    }
}
