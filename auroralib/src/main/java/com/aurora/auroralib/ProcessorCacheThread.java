package com.aurora.auroralib;

import android.util.Log;

/**
 * This class should be used to call the CacheServiceCaller
 * Using a separate thread is necessary to avoid threading issues (OnServiceBound in CacheServiceCaller
 * is automatically called on main thread, which leads to threading/waiting dificulties)
 */
public class ProcessorCacheThread extends Thread {
    private static final String TAG = ProcessorCacheThread.class.getSimpleName();
    protected int mCacheResult = -1000; // - 1000 means that the cache service from Aurora has not been reached
    protected PluginObject mPluginObject;
    private CacheServiceCaller mCacheServiceCaller;

    public ProcessorCacheThread(PluginObject pluginObject, CacheServiceCaller cacheServiceCaller) {
        this.mPluginObject = pluginObject;
        this.mCacheServiceCaller = cacheServiceCaller;
    }

    public int getCacheResult() {
        return mCacheResult;
    }

    protected int cache(){
        return mCacheServiceCaller.cacheOperation(mPluginObject.mFileName,
                mPluginObject.mUniquePluginName, mPluginObject.toJSON());
    }

    /**
     * This methad can be overridden to take particular actions depending on the cache result.
     * Currently tries caching again if it failed during its first attempt.
     *
     * @param cacheResult result of the first caching operation. 0 means success.
     */
    protected void handleCacheResult(int cacheResult){
        if (cacheResult != 0) {
            int secondResult = cache();
            Log.d(TAG, "Second cache operation result: " + secondResult);
        }
    }

    public void run() {
        int cacheResult = cache();
        Log.d(TAG, "" + cacheResult);
        handleCacheResult(cacheResult);
    }
}