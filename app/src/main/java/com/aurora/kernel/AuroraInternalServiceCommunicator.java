package com.aurora.kernel;

import com.aurora.internalservice.internalcache.InternalCache;
import com.aurora.kernel.event.CacheFileRequest;
import com.aurora.kernel.event.CacheFileResponse;
import com.aurora.plugin.ProcessedText;

import io.reactivex.Observable;

/**
 * Communicator that communicates with internal services offered to Aurora
 */
public class AuroraInternalServiceCommunicator extends Communicator {
    private InternalCache mInternalCache;

    private Observable<CacheFileRequest> mCacheFileRequestObservable;

    public AuroraInternalServiceCommunicator(Bus bus, InternalCache internalCache) {
        super(bus);
        mInternalCache = internalCache;

        // Subscribe to incoming cache requests
        mCacheFileRequestObservable = mBus.register(CacheFileRequest.class);

        // When event comes in, call the appropriate handle method
        mCacheFileRequestObservable.subscribe(cacheFileRequest -> cacheFile(cacheFileRequest.getFileRef(),
                cacheFileRequest.getText(), cacheFileRequest.getUniquePluginName()));
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
}
