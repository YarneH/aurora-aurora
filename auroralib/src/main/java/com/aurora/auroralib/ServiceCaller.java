package com.aurora.auroralib;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.util.Log;

import java.util.List;

public abstract class ServiceCaller implements ServiceConnection {
    /**
     * Context used for binding
     */
    protected Context mAppContext;
    /**
     * Object used for synchronisation
     */
    protected final Object mMonitor = new Object();

    /**
     * Boolean indicating if service is connected
     */
    protected boolean mServiceConnected = false;


    public ServiceCaller(Context context){ mAppContext = context;}


    /**
     * Binds the service so that a call to the AIDL defined function cache(String) can be executed
     */
    protected void bindService(Class c, String logTag) {
        Intent implicit = new Intent(c.getName());
        List<ResolveInfo> matches = mAppContext.getPackageManager().queryIntentServices(implicit, 0);

        if (matches.isEmpty()) {
            Log.d(logTag, "Service not found");
        } else if (matches.size() > 1) {
            Log.d(logTag, "Multiple of these services found");
        } else {
            Log.d(logTag, "1 of these services found");
            Intent explicit = new Intent(implicit);
            ServiceInfo svcInfo = matches.get(0).serviceInfo;
            ComponentName cn = new ComponentName(svcInfo.applicationInfo.packageName,
                    svcInfo.name);

            explicit.setComponent(cn);
            mAppContext.bindService(explicit, this, Context.BIND_AUTO_CREATE);
            Log.d(logTag, "Binding service");
        }
    }

    /**
     * Release the binding
     */
    protected void unbindService() {
        mAppContext.unbindService(this);
        disconnect();
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

    protected abstract void disconnect();
}
