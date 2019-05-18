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

/**
 * Class used by plugins to call the cache service of Aurora. Should not be used directly. Instead
 * calls to the service should be handled by a ProcessorCacheThread.
 */
public class CacheServiceCaller extends ServiceCaller {
    /**
     * Tag used for log messages
     */
    private static final String LOG_TAG = CacheServiceCaller.class.getSimpleName();

    /**
     * Binding to the remote interface
     */
    private ICache mCacheBinding = null;


    /**
     * Constructor for a CacheServiceCaller
     *
     * @param context Context used for binding
     */
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
     * @return status code of the cache operation from CacheService in Aurora Internal Services
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
     * This function will be called by the android system and sets the binding
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

        /**
         * Application context, required for writing to internal storage
         */
        private Context mContext;

        /**
         * The result of the caching operation, corresponding to a value in CacheResults
         */
        private int mCacheResult = CacheResults.NOT_REACHED;
        /**
         * The name of the original file of the PluginObject to be cached
         */
        private String mFileName;
        /**
         * The unique plugin name of the plugin that is executing the caching operation
         */
        private String mUniquePluginName;
        /**
         * The PluginObject to be cached in JSON format
         */
        private String mPluginObjectJSON;

        /**
         * Constructs a CacheThread
         *
         * @param context           An application context object
         * @param fileName          The name of the original file of the PluginObject to be cached
         * @param uniquePluginName  The unique plugin name of the plugin that is executing the caching
         *                          operation
         * @param pluginObjectJSON  The PluginObject to be cached in JSON format
         */
        CacheThread(final Context context, String fileName, String uniquePluginName,
                    String pluginObjectJSON) {
            mContext = context;
            mFileName = fileName;
            mUniquePluginName = uniquePluginName;
            mPluginObjectJSON = pluginObjectJSON;
        }

        /**
         * Getter used to query the result of the caching operation since {@link CacheThread#run()}
         * returns void
         *
         * @return The result code of the caching operation
         */
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

                // I don't think the if ever happens
                if (mCacheBinding == null) {
                    synchronized (mMonitor) {
                        Log.v(LOG_TAG, "Entering sync block" + mCacheResult);

                        mCacheResult = cache();
                    }
                } else {
                    mCacheResult = mCacheBinding.cache(mFileName, mUniquePluginName,
                            writeToAurora());

                    Log.v(LOG_TAG, "" + mCacheResult);
                }

            } catch (RemoteException e) {
                Log.e(LOG_TAG, "Exception requesting cache", e);
                mCacheResult = CacheResults.REMOTE_FAIL;
            } catch (IOException e) {
                Log.e(LOG_TAG, "Exception writing to internal storage", e);
                mCacheResult = CacheResults.CACHE_FAIL;
            }
        }

        /**
         * Executes the caching service by calling the cache operation on the binding, which executes
         * the cache service in Aurora.
         * If there is no connection to the service yet, this function waits until it is connected
         *
         * @return                  an int corresponding to a value in CacheResults
         * @throws RemoteException  is thrown by {@link CacheThread#writeToAurora()}
         * @throws IOException      is thrown when waiting for the connection to the service gets interrupted
         */
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
