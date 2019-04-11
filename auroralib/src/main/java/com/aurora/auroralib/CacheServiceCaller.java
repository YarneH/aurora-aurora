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
        Log.d("PLUGIN", "cache called");
        int cacheResult = -1000;
        try {
            if (mBinding != null) {
                cacheResult = mBinding.cache(pluginObjectJSON);
            }
        }
        catch (RemoteException e) {
            Log.e(getClass().getSimpleName(), "Exception requesting cache", e);
            cacheResult = -1;
        }

        return cacheResult;
    }

    /*
    unbindService(AuroraService.CACHE);
        disconnect();
     */


    @Override
    public void onServiceConnected(ComponentName className, IBinder binder) {
        mBinding = ICache.Stub.asInterface(binder);
        Log.d("PLUGIN", "Bound");
    }

    @Override
    public void onServiceDisconnected(ComponentName className) {
        disconnect();

    }

    private void disconnect() {
        mBinding=null;
        Log.d("PLUGIN", "UnBound");
    }

}
