package com.aurora.internalservice.internalcache;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class CacheService extends Service {

    @Override
    public IBinder onBind(Intent intent) {
        //Toast.makeText(getApplicationContext(), "binding", Toast.LENGTH_SHORT).show();
        return(new CacheBinder());
    }



    private static class CacheBinder extends ICache.Stub {

        @Override
        public int cache(String pluginObject) {
            CacheThread ct = new CacheThread(pluginObject);
            ct.start();
            return ct.getReturnCode();
        }
    }

    private static class CacheThread extends Thread {
        private String pluginObject=null;
        private int returnCode = -1;
        //private Context applicationContext = null;

        CacheThread(String pluginObject) {
            this.pluginObject=pluginObject;
        }

        public int getReturnCode(){
            return returnCode;
        }

        @Override
        public void run() {
            //Toast.makeText(applicationContext, pluginObject, Toast.LENGTH_SHORT).show();
            // TODO: Cache the file

            Log.d("AURORA_CACHE", "SERVICE IS BEING RUN FOR:" + pluginObject);
            returnCode = 0;
        }
    }
}
