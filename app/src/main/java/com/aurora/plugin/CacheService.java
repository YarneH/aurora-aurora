package com.aurora.plugin;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.aurora.internalservice.internalcache.ICache;
import com.aurora.kernel.ContextNullException;
import com.aurora.kernel.Kernel;
import com.aurora.kernel.ProcessingCommunicator;

public class CacheService extends Service {

    @Override
    public IBinder onBind(Intent intent) {
        return(new CacheBinder());
    }



    private class CacheBinder extends ICache.Stub {

        @Override
        public int cache(String fileName, String pluginObject, String uniquePluginName) {
            Log.d("AURORA_CACHE", "SERVICE IS BEING RUN FOR:" + fileName + "\n" +
                    uniquePluginName  + "\n" + pluginObject);

            // Get the kernel and appropriate communicator
            Kernel kernel = null;
            try {
                kernel = Kernel.getInstance(CacheService.this.getApplicationContext());


                ProcessingCommunicator processingCommunicator = kernel.getProcessingCommunicator();

                return processingCommunicator.cacheFile(fileName, pluginObject, uniquePluginName);
            } catch (ContextNullException e) {
                Log.e("CacheService", "The kernel was not initialized with a valid context", e);
                return -2;
            }
        }
    }
}
