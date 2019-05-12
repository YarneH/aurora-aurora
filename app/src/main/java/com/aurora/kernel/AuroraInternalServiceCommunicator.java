package com.aurora.kernel;

import android.support.annotation.NonNull;
import android.util.Log;

import com.aurora.internalservice.internalcache.CachedFileInfo;
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
import com.aurora.kernel.event.UpdateCachedFileDateRequest;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;

/**
 * Communicator that communicates with internal services offered to Aurora
 */
public class AuroraInternalServiceCommunicator extends Communicator {
    /**
     * A tag used for logging
     */
    private static final String LOG_TAG = "AuroraIntrnlSvcComm";

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
     * Observable keeping track of incoming requests to remove files from the cache
     */
    private Observable<RemoveFromCacheRequest> mRemoveFromCacheRequestObservable;

    /**
     * Observable keeping track of incoming requests to update the lastOpenedDate of files in the cache
     */
    private Observable<UpdateCachedFileDateRequest> mUpdateCachedFileDateRequestObservable;

    /**
     * Creates an AuroraInternalServiceCommunicator. There should be only one instance at a time
     *
     * @param bus           a reference to the unique bus instance over which the communicators will communicate events
     * @param internalCache a reference to the internal cache
     */
    public AuroraInternalServiceCommunicator(@NonNull final Bus bus, @NonNull final InternalCache internalCache) {
        super(bus);
        mInternalCache = internalCache;

        // Subscribe to incoming cache requests
        mCacheFileRequestObservable = mBus.register(CacheFileRequest.class);

        // When event comes in, call the appropriate handle method
        mCacheFileRequestObservable.subscribe(cacheFileRequest -> cacheFile(cacheFileRequest.getFileRef(),
                cacheFileRequest.getPluginObject(), cacheFileRequest.getUniquePluginName()),
                error -> Log.e(LOG_TAG, "Something went wrong caching the file", error));

        // Subscribe to incoming query requests
        mQueryCacheRequestObservable = mBus.register(QueryCacheRequest.class);

        // Call appropriate handle method when request comes in
        mQueryCacheRequestObservable.subscribe((QueryCacheRequest queryCacheRequest) -> {
            if (queryCacheRequest.isFullCacheRequest()) {
                queryFullCache(queryCacheRequest.getMaxEntries());
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

        // Subscribe to incoming requests to update the date of files
        mUpdateCachedFileDateRequestObservable = mBus.register(UpdateCachedFileDateRequest.class);

        // Call appropriate method when request comes in
        mUpdateCachedFileDateRequestObservable.subscribe(request -> updateDate(request.getFileRef(),
                request.getUniquePluginName()));
    }

    /**
     * Private handle method that handles CacheFileRequests
     *
     * @param fileRef          a reference to the file that needs to be cached
     * @param pluginObject     the processed text representation that needs to be cached
     * @param uniquePluginName the name of the plugin that built the representation
     */
    private void cacheFile(@NonNull final String fileRef, @NonNull final String pluginObject,
                           @NonNull final String uniquePluginName) {
        // Cache file
        boolean cacheSuccess = mInternalCache.cacheFile(fileRef, pluginObject, uniquePluginName);

        // Create response and post it
        CacheFileResponse response = new CacheFileResponse(cacheSuccess);
        mBus.post(response);
    }

    /**
     * Private handle method to query the cache for all files
     *
     * @param maxEntries maximum number of entries that should be queried
     */
    private void queryFullCache(final int maxEntries) {
        // Get all files from cache
        List<CachedFileInfo> processedFiles = mInternalCache.getFullCache(maxEntries);

        // Wrap in response and post on the bus
        QueryCacheResponse response = new QueryCacheResponse(processedFiles);
        mBus.post(response);
    }

    /**
     * Private handle method that queries cache for specific file processed with a certain plugin
     *
     * @param fileRef          a reference to the file to check if it was already cached (should be hash_displayName)
     *                         Check the getFileName method from MainActivity.
     * @param uniquePluginName the plugin that the file should be processed with
     */
    private void queryCache(@NonNull final String fileRef, @NonNull final String uniquePluginName) {
        CachedFileInfo processedFile = mInternalCache.checkCacheForProcessedFile(fileRef, uniquePluginName);

        // Create response event with result in list, or empty list if result was null
        List<CachedFileInfo> cachedProcessedFiles = new ArrayList<>();
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
     * @param fileRef          a reference to the file to retrieve (should be hash_displayName)
     *                         Check the getFileName method from MainActivity.
     * @param uniquePluginName the plugin that the file was processed with
     */
    private void retrieveFileFromCache(@NonNull final String fileRef, @NonNull final String uniquePluginName) {
        CachedProcessedFile processedFile = mInternalCache.retrieveFile(fileRef, uniquePluginName);

        // Create response event and post on bus
        RetrieveFileFromCacheResponse response;

        if (processedFile != null) {
            response = new RetrieveFileFromCacheResponse(processedFile);
        } else {
            response = new RetrieveFileFromCacheResponse(
                    new CachedProcessedFile("{}", fileRef, uniquePluginName));
        }

        // Post response on bus
        mBus.post(response);
    }

    /**
     * Private handle method that removes a specific file from the cache
     *
     * @param fileRef          a reference to the file to remove (should be hash_displayName)
     *                         Check the getFileName method from MainActivity.
     * @param uniquePluginName the name of the plugin that the file was processed with
     */
    private void removeFileFromCache(@NonNull final String fileRef, @NonNull final String uniquePluginName) {
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
    private void clearPluginCache(@NonNull final String uniquePluginName) {
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

    /**
     * Updates the date (to now) of a file if it is in the cache
     *
     * @param fileRef          a reference to the originally processed file
     * @param uniquePluginName the name of the plugin that the file was processed with
     */
    private void updateDate(@NonNull final String fileRef, @NonNull final String uniquePluginName) {
        mInternalCache.updateCachedFileDate(fileRef, uniquePluginName);
    }
}
