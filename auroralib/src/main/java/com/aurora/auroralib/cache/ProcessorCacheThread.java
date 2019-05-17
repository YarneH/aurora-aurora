package com.aurora.auroralib.cache;

import android.util.Log;

import com.aurora.auroralib.PluginObject;

/**
 * This class should be used to call the CacheServiceCaller
 * Using a separate thread is necessary to avoid threading issues (OnServiceBound in CacheServiceCaller
 * is automatically called on main thread, which leads to threading/waiting difficulties)
 *
 * This caching operation itself is actually already called by ProcessorCommunicator and plugins should
 * normally not need to call this themselves.
 */
public class ProcessorCacheThread extends Thread {
    /**
     * Tag used for logging
     */
    private static final String TAG = ProcessorCacheThread.class.getSimpleName();

    /**
     * Result of caching operation
     */
    protected int mCacheResult = CacheResults.NOT_REACHED;

    /**
     * Plugin object containing processed text
     */
    protected PluginObject mPluginObject;

    /**
     * a reference to the cache service caller that is responsible for actually calling the cache
     */
    private CacheServiceCaller mCacheServiceCaller;


    /**
     * Constructor for the ProcessorCacheThread
     *
     * @param pluginObject          PluginObject to be cached
     * @param cacheServiceCaller    CacheServiceCaller used to invoke Aurora's caching service
     */
    public ProcessorCacheThread(PluginObject pluginObject, CacheServiceCaller cacheServiceCaller) {
        this.mPluginObject = pluginObject;
        this.mCacheServiceCaller = cacheServiceCaller;
    }

    public int getCacheResult() {
        return mCacheResult;
    }

    /**
     * Executes the cache operation
     *
     * @return int corresponding to a value of CacheResults, functioning as an error/success code
     * (int is used because of AIDL)
     */
    protected int cache() {
        return mCacheServiceCaller.cacheOperation(mPluginObject.getFileName(),
                mPluginObject.getUniquePluginName(), mPluginObject.toJSON());
    }

    /**
     * This method can be overridden to take particular actions depending on the cache result.
     * Currently tries caching again if it failed during its first attempt.
     *
     * @param cacheResult result of the first caching operation. 0 means success.
     */
    protected void handleCacheResult(int cacheResult) {
        if (cacheResult != CacheResults.CACHE_SUCCESS) {
            int secondResult = cache();
            Log.d(TAG, "Second cache operation result: " + secondResult);
        }
    }

    /**
     * Runs the thread which performs the caching operation and executes handleCacheResult afterwards
     */
    @Override
    public void run() {
        int cacheResult = cache();
        Log.d(TAG, "" + cacheResult);
        handleCacheResult(cacheResult);
    }
}
