package com.aurora.plugin;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.aurora.internalservice.internalcache.ICache;

public class CacheService extends Service {

    @Override
    public IBinder onBind(Intent intent) {
        //Toast.makeText(getApplicationContext(), "binding", Toast.LENGTH_SHORT).show();
        return(new CacheBinder());
    }



    private static class CacheBinder extends ICache.Stub {

        @Override
        public int cache(String fileName, String uniquePluginName, String pluginObject) {
            Log.d("AURORA_CACHE", "SERVICE IS BEING RUN FOR:" + fileName + "\n" +
                    uniquePluginName  + "\n" + pluginObject);

            // Get the kernel

            return 0;

        }
    }
}
