package com.aurora.plugin;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.aurora.auroralib.cache.CacheResults;
import com.aurora.internalservice.internalcache.ICache;
import com.aurora.kernel.ContextNullException;
import com.aurora.kernel.Kernel;
import com.aurora.kernel.ProcessingCommunicator;

/**
 * Service that handles caching requests
 */
public class CacheService extends Service {

    /**
     * Required function for Service usage. Gets called by the Android platform.
     *
     * @param intent Service Intent for the cache service
     * @return a CacheBinder to be used in the plugin
     */
    @Override
    public IBinder onBind(Intent intent) {
        return(new CacheBinder());
    }

    /**
     * Binder for the service, makes use of AIDL file
     */
    private class CacheBinder extends ICache.Stub {

        /**
         * Requests a cache operation to be executed by the processing communicator
         *
         * @param fileRef          a reference to the original file
         * @param pluginObject     a json representation of the object that needs to be cached
         * @param uniquePluginName the name of the plugin that the file was processed with
         * @return a status code indicating if the cache operation was successful (0) or not (-1)
         */
        @Override
        public int cache(String fileRef, String pluginObject, String uniquePluginName) {
            Log.d("AURORA_CACHE", "SERVICE IS BEING RUN FOR:" + fileRef + "\n" +
                    uniquePluginName  + "\n" + pluginObject);

            // Get the kernel and appropriate communicator
            try {
                ProcessingCommunicator processingCommunicator = Kernel.getInstance(
                        CacheService.this.getApplicationContext()).getProcessingCommunicator();

                return processingCommunicator.cacheFile(fileRef, pluginObject, uniquePluginName);
            } catch (ContextNullException e) {
                Log.e("CacheService", "The kernel was not initialized with a valid context", e);
                return CacheResults.KERNEL_FAIL;
            }
        }
    }
}
