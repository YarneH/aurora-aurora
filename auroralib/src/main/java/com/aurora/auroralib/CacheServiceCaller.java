package com.aurora.auroralib;

import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.aurora.internalservice.internalcache.ICache;

import java.util.List;

public class CacheServiceCaller implements ServiceConnection {
    private ICache mBinding = null;
    //private ICache cacheBinding = null;
    // !!! Not sure yet if this is handled right by just passing an activity's context (See BasicPlugin_Old)
    private Context mAppContext = null;
    //PackageManager pm = getPackageManager();
    private Object monitor = new Object();

    public CacheServiceCaller(Context context){
        mAppContext = context;
    }


    //TODO See how to integrate this properly (should be called form processor so skip activities
    public void bindService(){
        Intent implicit = new Intent(ICache.class.getName());
        //Intent implicit = new Intent(IDownload.class.getName());
        List<ResolveInfo> matches = mAppContext.getPackageManager().queryIntentServices(implicit, 0);
        if (matches.size() == 0) {
            Log.d("PLUGIN_CACHE_SERVICE", "No cache service found");
        }
        else if (matches.size() > 1) {
            Log.d("PLUGIN_CACHE_SERVICE", "Multiple cache services found");
        }
        else {
            Log.d("PLUGIN_CACHE_SERVICE", "1 cache services found");
            Intent explicit = new Intent(implicit);
            ServiceInfo svcInfo = matches.get(0).serviceInfo;
            ComponentName cn = new ComponentName(svcInfo.applicationInfo.packageName,
                    svcInfo.name);

            explicit.setComponent(cn);
            mAppContext.bindService(explicit, this, Context.BIND_AUTO_CREATE);
            Log.d("PLUGIN", "Binding");
        }
    }

    public void unbindService(){
        mAppContext.unbindService(this);
        disconnect();
    }

    public int cache(String pluginObjectJSON){
        CacheThread cacheThread = new CacheThread(pluginObjectJSON);
        cacheThread.start();
        /*try {
            cacheThread.join();
        } catch (InterruptedException e){
            Log.e(getClass().getSimpleName(), "Exception requesting cache", e);
        }*/

        return cacheThread.getCacheResult();
    }

    /*
    unbindService(AuroraService.CACHE);
        disconnect();
     */


    @Override
    public void onServiceConnected(ComponentName className, IBinder binder) {
        synchronized (monitor) {
            mBinding = ICache.Stub.asInterface(binder);
            Log.d("PLUGIN", "Bound");

            monitor.notify();
        }
    }

    @Override
    public void onServiceDisconnected(ComponentName className) {
        disconnect();

    }

    private void disconnect() {
        mBinding=null;
        Log.d("PLUGIN", "UnBound");
    }

    private class CacheThread extends Thread {
        private int mCacheResult = -1000;
        private String pluginObjectJSON;

        protected CacheThread(String pluginObjectJSON){
            this.pluginObjectJSON = pluginObjectJSON;
        }

        protected int getCacheResult() {
            return mCacheResult;
        }

        public void run(){
            Log.d("PLUGIN", "cache called");
            try {
                /*
                int countSleepIterations = 0;
                while (mBinding == null && countSleepIterations < 5){
                    try {
                        sleep(500);
                        countSleepIterations++;
                    } catch (InterruptedException e){
                        break;
                    }
                }*/



                if (mBinding == null) {
                    synchronized (monitor) {
                        Log.d("PLUGIN", "Entering sync block" + mCacheResult);
                        try {
                            monitor.wait();
                        } catch (InterruptedException e) {
                            Log.e(getClass().getSimpleName(), "Exception requesting cache", e);
                        }
                        mCacheResult = mBinding.cache(pluginObjectJSON);
                        Log.d("PLUGIN", "" + mCacheResult);
                    }

                    //Log.e(getClass().getSimpleName(), "Number of sleep iterations exceeded");
                }
                // TODO remove
                else {
                    mCacheResult = mBinding.cache(pluginObjectJSON);
                    Log.d("PLUGIN", "" + mCacheResult);

                }

            }
            catch (RemoteException e) {
                Log.e(getClass().getSimpleName(), "Exception requesting cache", e);
                mCacheResult = -1;
            }
        }
    }

}
