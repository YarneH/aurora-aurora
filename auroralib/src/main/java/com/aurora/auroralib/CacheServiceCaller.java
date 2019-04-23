package com.aurora.auroralib;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.aurora.internalservice.internalcache.ICache;

import java.util.List;

public class CacheServiceCaller implements ServiceConnection {
    private static final String LOG_TAG = CacheServiceCaller.class.getSimpleName();

    private ICache mBinding = null;
    //private ICache cacheBinding = null;
    // !!! Not sure yet if this is handled right by just passing an activity's context (See BasicPlugin_Old)
    private Context mAppContext;
    //PackageManager pm = getPackageManager();
    private Object monitor = new Object();

    public CacheServiceCaller(Context context) {
        mAppContext = context;
    }


    public int cacheOperation(String pluginObjectJSON){
        synchronized (monitor) {
            int result = -1000;
            bindService();
            try {
                monitor.wait();
                result = cache(pluginObjectJSON);
                unbindService();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            //unbindService();
            return result;
        }
    }


    /**
     * Binds the service so that a call to the AIDL defined function cache(String) can be executed
     */
    private void bindService() {
        Intent implicit = new Intent(ICache.class.getName());
        //Intent implicit = new Intent(IDownload.class.getName());
        List<ResolveInfo> matches = mAppContext.getPackageManager().queryIntentServices(implicit, 0);
        if (matches.size() == 0) {
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
     *
     * @param pluginObjectJSON the JSON string of the object that needs to be cached
     * @return status code of the cache operation from Cache Service in Aurora Internal Services
     */
    private int cache(String pluginObjectJSON) {
        CacheThread cacheThread = new CacheThread(pluginObjectJSON);
        cacheThread.start();
        try {
            cacheThread.join();
        } catch (InterruptedException e){
            Log.e(getClass().getSimpleName(), "Exception requesting cache", e);
        }
        return cacheThread.getCacheResult();
    }

    /*
    unbindService(AuroraService.CACHE);
        disconnect();
     */


    /**
     * This function will be called by the android system
     *
     * @param className
     * @param binder
     *
     * Finishes the binding process
     */
    @Override
    public void onServiceConnected(ComponentName className, IBinder binder) {
        synchronized (monitor) {
            mBinding = ICache.Stub.asInterface(binder);
            Log.d(LOG_TAG, "Plugin Bound");

            monitor.notify();
        }
    }

    /**
     * This function is called by the android system if the service gets disconnected
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
        mBinding = null;
        Log.d(LOG_TAG, "Plugin Unbound");
    }

    private class CacheThread extends Thread {
        private int mCacheResult = -1000; // - 1000 means that the cache service from Aurora has not been reached
        private String pluginObjectJSON;

        protected CacheThread(String pluginObjectJSON) {
            this.pluginObjectJSON = pluginObjectJSON;
        }

        protected int getCacheResult() {
            return mCacheResult;
        }

        public void run() {
            Log.d(LOG_TAG, "cache called");
            try {

                if (mBinding == null) {
                    synchronized (monitor) {
                        Log.d(LOG_TAG, "Entering sync block" + mCacheResult);
                        try {
                            monitor.wait();
                        } catch (InterruptedException e) {
                            Log.e(getClass().getSimpleName(), "Exception requesting cache", e);
                        }
                        mCacheResult = mBinding.cache(pluginObjectJSON);
                        Log.d(LOG_TAG, "" + mCacheResult);
                    }
                }
                else {
                    mCacheResult = mBinding.cache(pluginObjectJSON);
                    Log.d(LOG_TAG, "" + mCacheResult);
                }


            } catch (RemoteException e) {
                Log.e(getClass().getSimpleName(), "Exception requesting cache", e);
                mCacheResult = -1;
            }
            //unbindService();
        }
    }

}
