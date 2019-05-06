package com.aurora.auroralib.cache;

import android.util.Log;

import com.aurora.auroralib.PluginObject;

/**
 * This class should be used to call the CacheServiceCaller
 * Using a separate thread is necessary to avoid threading issues (OnServiceBound in CacheServiceCaller
 * is automatically called on main thread, which leads to threading/waiting dificulties)
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

    public ProcessorCacheThread(PluginObject pluginObject, CacheServiceCaller cacheServiceCaller) {
        this.mPluginObject = pluginObject;
        this.mCacheServiceCaller = cacheServiceCaller;
    }

    public int getCacheResult() {
        return mCacheResult;
    }

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
        if (cacheResult != 0) {
            int secondResult = cache();
            Log.d(TAG, "Second cache operation result: " + secondResult);
        }
    }

    @Override
    public void run() {
        int cacheResult = cache();
        Log.d(TAG, "" + cacheResult);
        handleCacheResult(cacheResult);
    }
}
