package com.aurora.auroralib.cache;

import android.content.ComponentName;
import android.content.Context;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.util.Log;

import com.aurora.auroralib.ServiceCaller;
import com.aurora.internalservice.internalcache.ICache;

public class CacheServiceCaller extends ServiceCaller {
    /**
     * Tag used for log messages
     */
    private static final String LOG_TAG = CacheServiceCaller.class.getSimpleName();

    /**
     * Binding to the remote interface
     */
    private ICache mCacheBinding = null;


    public CacheServiceCaller(Context context) {
        super(context);
    }


    /**
     * Tries to cache a file in aurora through the service
     *
     * @param fileName         the name of the file that contained the plain text
     * @param uniquePluginName the name of the plugin that the file was processed with
     * @param pluginObjectJSON the json representation of the processed text
     * @return a status code indicating whether or not the operation was successful
     */
    int cacheOperation(@NonNull String fileName, @NonNull String uniquePluginName,
                       @NonNull String pluginObjectJSON) {
        synchronized (mMonitor) {
            int result = CacheResults.NOT_REACHED;
            bindService(ICache.class, LOG_TAG);
            try {
                while (!mServiceConnected) {
                    mMonitor.wait();
                }

                result = cache(fileName, uniquePluginName, pluginObjectJSON);
                unbindService();
            } catch (InterruptedException e) {
                Log.e(LOG_TAG, "cacheOperation was interrupted!", e);

                // Restore the interrupted state:
                // https://www.ibm.com/developerworks/java/library/j-jtp05236/index.html?ca=drs-#2.1
                Thread.currentThread().interrupt();
            }

            return result;
        }
    }

    /**
     * Will start a new thread to cache the file
     *
     * @param fileName         the name of the file that contained the original plain text
     * @param uniquePluginName the name of the plugin that processed the file
     * @param pluginObjectJSON the JSON string of the object that needs to be cached
     * @return status code of the cache operation from Cache Service in Aurora Internal Services
     */
    private int cache(@NonNull String fileName, @NonNull String uniquePluginName,
                      @NonNull String pluginObjectJSON) {
        CacheThread cacheThread = new CacheThread(fileName, uniquePluginName, pluginObjectJSON);
        cacheThread.start();
        try {
            cacheThread.join();
        } catch (InterruptedException e) {
            Log.e(getClass().getSimpleName(), "Exception requesting cache", e);

            // Restore the interrupted state:
            // https://www.ibm.com/developerworks/java/library/j-jtp05236/index.html?ca=drs-#2.1
            Thread.currentThread().interrupt();
        }
        return cacheThread.getCacheResult();
    }

    /**
     * This function will be called by the android system
     *
     * @param className Name of the class
     * @param binder    Finishes the binding process
     */
    @Override
    public void onServiceConnected(ComponentName className, IBinder binder) {
        synchronized (mMonitor) {
            mCacheBinding = ICache.Stub.asInterface(binder);
            Log.i(LOG_TAG, "Plugin Bound");

            mServiceConnected = true;
            mMonitor.notifyAll();
        }
    }

    /**
     * Release the binding
     */
    @Override
    protected void disconnect() {
        mServiceConnected = false;
        mCacheBinding = null;
        Log.i(LOG_TAG, "Plugin Unbound");
    }

    /**
     * A private thread class that will cache the file in another thread to avoid blocking of the
     * main thread
     */
    private class CacheThread extends Thread {
        private int mCacheResult = CacheResults.NOT_REACHED;
        private String mFileName;
        private String mUniquePluginName;
        private String mPluginObjectJSON;

        CacheThread(String fileName, String uniquePluginName, String pluginObjectJSON) {
            mFileName = fileName;
            mUniquePluginName = uniquePluginName;
            mPluginObjectJSON = pluginObjectJSON;
        }

        int getCacheResult() {
            return mCacheResult;
        }

        /**
         * Waits in case the binding is not ready and executes the cache operation
         */
        @Override
        public void run() {
            Log.i(LOG_TAG, "cache called");
            try {

                if (mCacheBinding == null) {
                    synchronized (mMonitor) {
                        Log.v(LOG_TAG, "Entering sync block" + mCacheResult);

                        mCacheResult = cache();
                    }
                } else {
                    mCacheResult = mCacheBinding.cache(mFileName, mPluginObjectJSON,
                            mUniquePluginName);
                    Log.v(LOG_TAG, "" + mCacheResult);
                }

            } catch (RemoteException e) {
                Log.e(getClass().getSimpleName(), "Exception requesting cache", e);
                mCacheResult = CacheResults.REMOTE_FAIL;
            }
        }

        /**
         * Caches by calling {@link ICache#cache(String, String, String)}.
         *
         * @return The resulting int from {@link CacheResults}
         * @throws RemoteException On trouble with the connection with Aurora
         */
        private int cache() throws RemoteException {
            synchronized (mMonitor) {
                try {
                    while (!mServiceConnected) {
                        mMonitor.wait();
                    }
                } catch (InterruptedException e) {
                    Log.e(getClass().getSimpleName(), "Exception requesting cache", e);

                    // Restore the interrupted state:
                    // https://www.ibm.com/developerworks/java/library/j-jtp05236/index
                    // .html?ca=drs-#2.1
                    Thread.currentThread().interrupt();
                }
                int cacheResult = mCacheBinding.cache(mFileName, mUniquePluginName,
                        mPluginObjectJSON);
                Log.v(LOG_TAG, "" + cacheResult);
                return cacheResult;
            }
        }
    }

}
