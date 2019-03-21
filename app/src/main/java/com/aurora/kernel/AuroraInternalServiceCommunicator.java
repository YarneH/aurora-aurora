package com.aurora.kernel;

import com.aurora.auroralib.PluginObject;
import com.aurora.internalservice.internalcache.CachedProcessedFile;
import com.aurora.internalservice.internalcache.InternalCache;
import com.aurora.kernel.event.CacheFileRequest;
import com.aurora.kernel.event.CacheFileResponse;
import com.aurora.kernel.event.QueryCacheRequest;
import com.aurora.kernel.event.QueryCacheResponse;
import com.aurora.kernel.event.RemoveFromCacheRequest;
import com.aurora.kernel.event.RemoveFromCacheResponse;
import com.aurora.kernel.event.RetrieveFileFromCacheRequest;
import com.aurora.kernel.event.RetrieveFileFromCacheResponse;

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

    /**
     * Observable keeping track of incoming requests to retrieve files
     */
    private Observable<RetrieveFileFromCacheRequest> mRetrieveFileFromCacheRequestObservable;

    /**
     * Observable keeping track of incoming request to remove files from the cache
     */
    private Observable<RemoveFromCacheRequest> mRemoveFromCacheRequestObservable;

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

        // Call appropriate handle method when request comes in
        mQueryCacheRequestObservable.subscribe((QueryCacheRequest queryCacheRequest) -> {
            if (queryCacheRequest.isFullCacheRequest()) {
                queryFullCache();
            } else {
                queryCache(queryCacheRequest.getFileRef(), queryCacheRequest.getUniquePluginName());
            }
        });

        // Subscribe to incoming retrieve requests
        mRetrieveFileFromCacheRequestObservable = mBus.register(RetrieveFileFromCacheRequest.class);

        // Call appropriate handle method when request comes in
        mRetrieveFileFromCacheRequestObservable.subscribe(
                cacheRequest -> retrieveFileFromCache(cacheRequest.getFileRef(),
                        cacheRequest.getUniquePluginName())
        );

        // Subscribe to incoming requests to remove files from the cache
        mRemoveFromCacheRequestObservable = mBus.register(RemoveFromCacheRequest.class);

        // Call appropriate handle method when request comes in
        mRemoveFromCacheRequestObservable.subscribe((RemoveFromCacheRequest request) -> {
            if (request.isClearCache()) {
                clearCache();
            } else if (request.getFileRef() == null) {
                clearPluginCache(request.getUniquePluginName());
            } else {
                removeFileFromCache(request.getFileRef(), request.getUniquePluginName());
            }
        });
    }

    /**
     * Private handle method that handles CacheFileRequests
     *
     * @param fileRef          a reference to the file that needs to be cached
     * @param processedText    the processed text representation that needs to be cached
     * @param uniquePluginName the name of the plugin that built the representation
     */
    private void cacheFile(String fileRef, PluginObject processedText, String uniquePluginName) {
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
        List<String> processedFiles = mInternalCache.getFullCache();

        // Wrap in response and post on the bus
        QueryCacheResponse response = new QueryCacheResponse(processedFiles);
        mBus.post(response);
    }

    /**
     * Private handle method that queries cache for specific file processed with a certain plugin
     *
     * @param fileRef          a reference to the file to check if it was already cached
     * @param uniquePluginName the plugin that the file should be processed with
     */
    private void queryCache(String fileRef, String uniquePluginName) {
        String processedFile = mInternalCache.checkCacheForProcessedFile(fileRef, uniquePluginName);

        // Create response event with result in list, or empty list if result was null
        List<String> cachedProcessedFiles = new ArrayList<>();
        if (processedFile != null) {
            cachedProcessedFiles.add(processedFile);
        }

        QueryCacheResponse response = new QueryCacheResponse(cachedProcessedFiles);

        // post response on bus
        mBus.post(response);
    }

    /**
     * Private handle method that retrieves a specific file processed with a certain plugin
     *
     * @param fileRef          a reference to the file to retrieve
     * @param uniquePluginName the plugin that the file was processed with
     */
    private void retrieveFileFromCache(String fileRef, String uniquePluginName) {
        CachedProcessedFile processedFile = mInternalCache.retrieveFile(fileRef, uniquePluginName);

        // Create response event and post on bus
        RetrieveFileFromCacheResponse response = new RetrieveFileFromCacheResponse(processedFile);

        // Post response on bus
        mBus.post(response);
    }

    /**
     * Private handle method that removes a specific file from the cache
     *
     * @param fileRef          a reference to the file to remove
     * @param uniquePluginName the name of the plugin that the file was processed with
     */
    private void removeFileFromCache(String fileRef, String uniquePluginName) {
        boolean success = mInternalCache.removeFile(fileRef, uniquePluginName);

        // Create response and post on bus
        RemoveFromCacheResponse response = new RemoveFromCacheResponse(success);
        mBus.post(response);
    }

    /**
     * Private handle method that removes all files from a given plugin from the cache
     *
     * @param uniquePluginName the name of the plugin to remove the files from
     */
    private void clearPluginCache(String uniquePluginName) {
        boolean success = mInternalCache.removeFilesByPlugin(uniquePluginName);

        // Create response and post on bus
        RemoveFromCacheResponse response = new RemoveFromCacheResponse(success);
        mBus.post(response);
    }

    /**
     * Private handle method that clears the enire cache
     */
    private void clearCache() {
        boolean success = mInternalCache.clear();

        // Create response and post on bus
        RemoveFromCacheResponse response = new RemoveFromCacheResponse(success);
        mBus.post(response);
    }
}
