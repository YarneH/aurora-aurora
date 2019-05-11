package com.aurora.plugin;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.IBinder;
import android.os.ParcelFileDescriptor;
import android.os.RemoteException;
import android.support.v4.content.FileProvider;
import android.util.Log;

import com.aurora.auroralib.cache.CacheResults;
import com.aurora.internalservice.internalcache.ICache;
import com.aurora.kernel.ContextNullException;
import com.aurora.kernel.Kernel;
import com.aurora.kernel.ProcessingCommunicator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

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
        return(new CacheBinder(this.getApplicationContext()));
    }

    /**
     * Binder for the service, makes use of AIDL file
     */
    private class CacheBinder extends ICache.Stub {
        private Context mContext;

        CacheBinder(final Context context) {
            super();
            this.mContext =context;
        }

        /**
         * Requests a cache operation to be executed by the processing communicator
         *
         * @param fileRef          a reference to the original file
         * @param pluginObject     a json representation of the object that needs to be cached
         * @param uniquePluginName the name of the plugin that the file was processed with
         * @return a status code indicating if the cache operation was successful (0) or not (-1)
         */
        @Override
        public int cache(String fileRef, String pluginObject, String uniquePluginName,
                         Intent intent) {
            Log.d("AURORA_CACHE", "SERVICE IS BEING RUN FOR:" + fileRef + "\n" +
                    uniquePluginName  + "\n" + pluginObject);

            Uri uri = intent.getData();

            // Open the file
            ParcelFileDescriptor inputPFD = null;
            try {
                inputPFD = mContext.getContentResolver().openFileDescriptor(uri,
                        "r");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            if(inputPFD == null) {
                throw new IllegalArgumentException("The file could not be opened");
            }

            // Read the file
            StringBuilder total = new StringBuilder();
            InputStream fileStream = new FileInputStream(inputPFD.getFileDescriptor());
            try (BufferedReader r = new BufferedReader(new InputStreamReader(fileStream))) {
                for (String line; (line = r.readLine()) != null; ) {
                    total.append(line).append('\n');
                }
            } catch (IOException e) {
                e.printStackTrace();
            }


            // Get the kernel and appropriate communicator
            try {
                ProcessingCommunicator processingCommunicator = Kernel.getInstance(
                        CacheService.this.getApplicationContext()).getProcessingCommunicator();

                return processingCommunicator.cacheFile(fileRef, total.toString(), uniquePluginName);
            } catch (ContextNullException e) {
                Log.e("CacheService", "The kernel was not initialized with a valid context", e);
                return CacheResults.KERNEL_FAIL;
            }
        }

        @Override
        public Intent getWritePermissionIntent() throws RemoteException {

            Intent intent = new Intent();

            Uri uri;

            try {
                File file = File.createTempFile("cached-", ".aur", mContext.getCacheDir());

                uri = FileProvider.getUriForFile(mContext, "com.aurora.aurora.provider", file);

            } catch (IOException e) {
                e.printStackTrace();
                throw new RemoteException("Something went wrong while creating the cache file");
            }

            mContext.grantUriPermission("com.aurora.basicplugin", uri,
                    Intent.FLAG_GRANT_WRITE_URI_PERMISSION + Intent.FLAG_GRANT_READ_URI_PERMISSION);
            mContext.grantUriPermission("com.aurora.auroralib", uri,
                    Intent.FLAG_GRANT_WRITE_URI_PERMISSION + Intent.FLAG_GRANT_READ_URI_PERMISSION);

            //intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            //intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setData(uri);

            return intent;
        }


    }
}
