package com.aurora.auroralib.cache;

import android.content.ComponentName;
import android.content.Context;
import android.net.Uri;
import android.os.IBinder;
import android.os.ParcelFileDescriptor;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.util.Log;

import com.aurora.auroralib.ServiceCaller;
import com.aurora.internalservice.internalcache.ICache;

import java.io.FileWriter;
import java.io.IOException;

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
        CacheThread cacheThread = new CacheThread(mAppContext, fileName, uniquePluginName,
                pluginObjectJSON);
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
            Log.d(LOG_TAG, "Plugin Bound");

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
        Log.d(LOG_TAG, "Plugin Unbound");
    }

    /**
     * A private thread class that will cache the file in another thread to avoid blocking of the
     * main thread
     */
    private class CacheThread extends Thread {

        /**
         * Application context, required for writing to internal storage
         */
        private Context mContext;

        private int mCacheResult = CacheResults.NOT_REACHED;
        private String mFileName;
        private String mUniquePluginName;
        private String mPluginObjectJSON;

        CacheThread(final Context context, String fileName, String uniquePluginName,
                    String pluginObjectJSON) {
            mContext = context;
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
            Log.d(LOG_TAG, "cache called");
            try {

                // I don't think the if ever happens
                if (mCacheBinding == null) {
                    synchronized (mMonitor) {
                        Log.d(LOG_TAG, "Entering sync block" + mCacheResult);

                        mCacheResult = cache();
                    }
                } else {
                    mCacheResult = mCacheBinding.cache(mFileName, mUniquePluginName,
                            writeToAurora());

                    Log.d(LOG_TAG, "" + mCacheResult);
                }

            } catch (RemoteException e) {
                Log.e(LOG_TAG, "Exception requesting cache", e);
                mCacheResult = CacheResults.REMOTE_FAIL;
            } catch (IOException e) {
                Log.e(LOG_TAG, "Exception writing to internal storage", e);
                mCacheResult = CacheResults.CACHE_FAIL;
            }
        }

        private int cache() throws RemoteException, IOException {
            synchronized (mMonitor) {
                try {
                    while (!mServiceConnected) {
                        mMonitor.wait();
                    }
                } catch (InterruptedException e) {
                    Log.e(LOG_TAG, "Exception requesting cache", e);

                    // Restore the interrupted state:
                    // https://www.ibm.com/developerworks/java/library/j-jtp05236/index
                    // .html?ca=drs-#2.1
                    Thread.currentThread().interrupt();
                }
                int cacheResult = mCacheBinding.cache(mFileName, mUniquePluginName,
                        writeToAurora());

                Log.d(LOG_TAG, "" + cacheResult);
                return cacheResult;
            }
        }

        /**
         * Writes the content that needs to cached to a file in the internal storage of Aurora on
         * a Uri received by Aurora. This Uri is returned.
         *
         * @return the Uri where the cache file is written to
         * @throws RemoteException on not receiving a Uri from Aurora
         * @throws IOException     on writing the file to internal storage
         */
        private Uri writeToAurora() throws RemoteException, IOException {
            // Get a Uri to a file in internal storage of Aurora.
            Uri uri = mCacheBinding.getWritePermissionUri( mContext.getPackageName());

            // Open the file
            ParcelFileDescriptor outputPFD = mContext.getContentResolver().openFileDescriptor(uri
                    , "w");

            if (outputPFD == null) {
                throw new IllegalArgumentException("The file could not be opened");
            }

            try (FileWriter fileWriter = new FileWriter(outputPFD.getFileDescriptor())) {
                fileWriter.write(mPluginObjectJSON);
            }

            return uri;
        }
    }

}
