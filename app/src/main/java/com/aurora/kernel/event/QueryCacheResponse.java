package com.aurora.kernel.event;

import java.util.List;

/**
 * This event contains the response (processed text) of a QueryCacheRequest.
 */
public class QueryCacheResponse extends Event {
    private List<String> mResults;

    public QueryCacheResponse(List<String> results) {
        mResults = results;
    }

    public List<String> getResults() {
        return mResults;
    }
}
