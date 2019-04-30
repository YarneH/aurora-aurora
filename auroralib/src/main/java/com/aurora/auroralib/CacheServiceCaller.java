package com.aurora.auroralib;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.util.Log;

import com.aurora.internalservice.internalcache.ICache;

import java.util.List;

public class CacheServiceCaller implements ServiceConnection {
    /**
     * Tag used for log messages
     */
    private static final String LOG_TAG = CacheServiceCaller.class.getSimpleName();

    /**
     * Binding to the remote interface
     */
    private ICache mBinding = null;

    // !!! Not sure yet if this is handled right by just passing an activity's context (See BasicPlugin_Old)
    private Context mAppContext;

    /**
     * Object used for synchronisation
     */
    private final Object monitor = new Object();

    /**
     * Boolean indicating if service is connected
     */
    private boolean mServiceConnected = false;

    public CacheServiceCaller(Context context) {
        mAppContext = context;
    }


    /**
     * Tries to cache a file in aurora through the service
     *
     * @param fileName         the name of the file that contained the plain text
     * @param uniquePluginName the name of the plugin that the file was processed with
     * @param pluginObjectJSON the json representation of the processed text
     * @return a status code indicating whether or not the operation was successful
     */
    public int cacheOperation(@NonNull String fileName, @NonNull String uniquePluginName,
                              @NonNull String pluginObjectJSON) {
        synchronized (monitor) {
            int result = CacheResults.NOT_REACHED;
            bindService();
            try {
                while (!mServiceConnected) {
                    monitor.wait();
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
     * Binds the service so that a call to the AIDL defined function cache(String) can be executed
     */
    private void bindService() {
        Intent implicit = new Intent(ICache.class.getName());
        List<ResolveInfo> matches = mAppContext.getPackageManager().queryIntentServices(implicit, 0);

        if (matches.isEmpty()) {
            Log.d(LOG_TAG, "No cache service found");
        } else if (matches.size() > 1) {
            Log.d(LOG_TAG, "Multiple cache services found");
        } else {
            Log.d(LOG_TAG, "1 cache service found");
            Intent explicit = new Intent(implicit);
            ServiceInfo svcInfo = matches.get(0).serviceInfo;
            ComponentName cn = new ComponentName(svcInfo.applicationInfo.packageName,
                    svcInfo.name);

            explicit.setComponent(cn);
            mAppContext.bindService(explicit, this, Context.BIND_AUTO_CREATE);
            Log.d(LOG_TAG, "Binding service");
        }
    }

    /**
     * Release the binding
     */
    private void unbindService() {
        mAppContext.unbindService(this);
        disconnect();
    }

    /**
     * Will start a new thread to cache the file
     *
     * @param fileName         the name of the file that contained the original plain text
     * @param uniquePluginName the name of the plugin that processed the file
     * @param pluginObjectJSON the JSON string of the object that needs to be cached
     * @return status code of the cache operation from Cache Service in Aurora Internal Services
     */
    private int cache(@NonNull String fileName, @NonNull String uniquePluginName, @NonNull String pluginObjectJSON) {
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
     * @param className
     * @param binder    Finishes the binding process
     */
    @Override
    public void onServiceConnected(ComponentName className, IBinder binder) {
        synchronized (monitor) {
            mBinding = ICache.Stub.asInterface(binder);
            Log.d(LOG_TAG, "Plugin Bound");

            mServiceConnected = true;
            monitor.notifyAll();
        }
    }

    /**
     * This function is called by the android system if the service gets disconnected
     *
     * @param className
     */
    @Override
    public void onServiceDisconnected(ComponentName className) {
        disconnect();
    }

    /**
     * Release the binding
     */
    private void disconnect() {
        mServiceConnected = false;
        mBinding = null;
        Log.d(LOG_TAG, "Plugin Unbound");
    }

    /**
     * A private thread class that will cache the file in another thread to avoid blocking of the main thread
     */
    private class CacheThread extends Thread {
        private int mCacheResult = CacheResults.NOT_REACHED;
        private String mFileName;
        private String mUniquePluginName;
        private String mPluginObjectJSON;

        protected CacheThread(String fileName, String uniquePluginName, String pluginObjectJSON) {
            mFileName = fileName;
            mUniquePluginName = uniquePluginName;
            mPluginObjectJSON = pluginObjectJSON;
        }

        protected int getCacheResult() {
            return mCacheResult;
        }

        @Override
        public void run() {
            Log.d(LOG_TAG, "cache called");
            try {

                if (mBinding == null) {
                    synchronized (monitor) {
                        Log.d(LOG_TAG, "Entering sync block" + mCacheResult);

                        mCacheResult = cache();
                    }
                } else {
                    mCacheResult = mBinding.cache(mFileName, mPluginObjectJSON, mUniquePluginName);
                    Log.d(LOG_TAG, "" + mCacheResult);
                }


            } catch (RemoteException e) {
                Log.e(getClass().getSimpleName(), "Exception requesting cache", e);
                mCacheResult = CacheResults.REMOTE_FAIL;
            }
        }

        private int cache() throws RemoteException {
            synchronized (monitor) {
                try {
                    while (!mServiceConnected) {
                        monitor.wait();
                    }
                } catch (InterruptedException e) {
                    Log.e(getClass().getSimpleName(), "Exception requesting cache", e);

                    // Restore the interrupted state:
                    // https://www.ibm.com/developerworks/java/library/j-jtp05236/index.html?ca=drs-#2.1
                    Thread.currentThread().interrupt();
                }
                int cacheResult = mBinding.cache(mFileName, mUniquePluginName, mPluginObjectJSON);
                Log.d(LOG_TAG, "" + cacheResult);
                return cacheResult;
            }
        }
    }

}
