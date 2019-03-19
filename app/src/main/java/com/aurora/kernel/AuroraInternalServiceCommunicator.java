package com.aurora.kernel;

import com.aurora.internalservice.internalcache.CachedProcessedFile;
import com.aurora.internalservice.internalcache.InternalCache;
import com.aurora.kernel.event.CacheFileRequest;
import com.aurora.kernel.event.CacheFileResponse;
import com.aurora.kernel.event.QueryCacheRequest;
import com.aurora.kernel.event.QueryCacheResponse;
import com.aurora.plugin.ProcessedText;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;

/**
 * Communicator that communicates with internal services offered to Aurora
 */
public class AuroraInternalServiceCommunicator extends Communicator {
    /**
     * reference to internal cache instance
     */
    private InternalCache mInternalCache;

    /**
     * Observable keeping track of incoming requests to cache files
     */
    private Observable<CacheFileRequest> mCacheFileRequestObservable;

    /**
     * Observable keeping track of incoming requests to query files
     */
    private Observable<QueryCacheRequest> mQueryCacheRequestObservable;

    public AuroraInternalServiceCommunicator(Bus bus, InternalCache internalCache) {
        super(bus);
        mInternalCache = internalCache;

        // Subscribe to incoming cache requests
        mCacheFileRequestObservable = mBus.register(CacheFileRequest.class);

        // When event comes in, call the appropriate handle method
        mCacheFileRequestObservable.subscribe(cacheFileRequest -> cacheFile(cacheFileRequest.getFileRef(),
                cacheFileRequest.getText(), cacheFileRequest.getUniquePluginName()));

        // Subscribe to incoming query requests
        mQueryCacheRequestObservable = mBus.register(QueryCacheRequest.class);
        mQueryCacheRequestObservable.subscribe(queryCacheRequest -> {
            if (queryCacheRequest.isFullCacheRequest()) {
                queryFullCache();
            } else {
                queryCache(queryCacheRequest.getFileRef(), queryCacheRequest.getUniquePluginName());
            }
        });
    }

    /**
     * Private handle method that handles CacheFileRequests
     *
     * @param fileRef a reference to the file that needs to be cached
     * @param processedText the processed text representation that needs to be cached
     * @param uniquePluginName the name of the plugin that built the representation
     */
    private void cacheFile(String fileRef, ProcessedText processedText, String uniquePluginName) {
        // Cache file
        boolean cacheSuccess = mInternalCache.cacheFile(fileRef, processedText, uniquePluginName);

        // Create response and post it
        CacheFileResponse response = new CacheFileResponse(cacheSuccess);
        mBus.post(response);
    }

    /**
     * Private handle method to query the cache for all files
     */
    private void queryFullCache() {
        // Get all files from cache
        List<CachedProcessedFile> processedFiles = mInternalCache.getFullCache();

        // Wrap in response and post on the bus
        QueryCacheResponse response = new QueryCacheResponse(processedFiles);
        mBus.post(response);
    }

    /**
     * Private handle method that queries cache for specific file processed with a certain plugin
     *
     * @param fileRef a reference to the file to check if it was already cached
     * @param uniquePluginName the plugin that the file should be processed with
     */
    private void queryCache(String fileRef, String uniquePluginName) {
        CachedProcessedFile processedFile = mInternalCache.checkCacheForProcessedFile(fileRef, uniquePluginName);

        // Create response event with result in list, or empty list if result was null
        List<CachedProcessedFile> cachedProcessedFiles = new ArrayList<>();
        if (processedFile != null) {
            cachedProcessedFiles.add(processedFile);
        }

        QueryCacheResponse response = new QueryCacheResponse(cachedProcessedFiles);

        // post response on bus
        mBus.post(response);
    }
}
