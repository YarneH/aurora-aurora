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
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Service that handles caching requests
 */
public class CacheService extends Service {

    /**
     * Tag for logging
     */
    private static final String CLASS_TAG = CacheService.class.getSimpleName();

    /**
     * Prefix used for temporary files that need to be cached
     */
    private static final String TRANSFER_PREFIX = "cache-request-";

    /**
     * File extension used as suffix
     */
    private static final String EXTENSION = ".aur";

    /**
     * Required function for Service usage. Gets called by the Android platform.
     *
     * @param intent Service Intent for the cache service
     * @return a CacheBinder to be used in the plugin
     */
    @Override
    public IBinder onBind(Intent intent) {
        return (new CacheBinder(this.getApplicationContext()));
    }

    /**
     * Binder for the service, makes use of AIDL file
     */
    private class CacheBinder extends ICache.Stub {


        /**
         * Application context, required for reading transferred files from internal storage.
         */
        private Context mContext;

        CacheBinder(final Context context) {
            super();
            this.mContext = context;
        }

        /**
         * Requests a cache operation to be executed by the processing communicator
         *
         * @param fileRef          a reference to the original file
         * @param uniquePluginName the name of the plugin that the file was processed with
         * @param uri              the uri on which the cached file is stored. This file is
         *                         assumed to be in internal storage of Aurora
         * @return a status code indicating if the cache operation was successful (0) or not (-1)
         */
        @Override
        public int cache(String fileRef, String uniquePluginName, Uri uri) {
            Log.d("AURORA_CACHE", "SERVICE IS BEING RUN FOR:" + fileRef + "\n" +
                    uniquePluginName);

            mContext.revokeUriPermission(uri, 0);
            removeFilesThatStartWithFromDir(mContext.getCacheDir(), "test-");
            removeFilesThatStartWithFromDir(mContext.getCacheDir(), "cached-");

            int cacheResult;
            // Get the kernel and appropriate communicator
            try {
                ProcessingCommunicator processingCommunicator = Kernel.getInstance(
                        CacheService.this.getApplicationContext()).getProcessingCommunicator();

                cacheResult = processingCommunicator.cacheFile(fileRef, readFile(uri),
                        uniquePluginName);
            } catch (ContextNullException e) {
                Log.e(CLASS_TAG, "The kernel was not initialized with a valid context", e);
                cacheResult = CacheResults.KERNEL_FAIL;
            } catch (IOException e) {
                Log.e(CLASS_TAG, "Transferring PluginObject to Aurora failed", e);
                cacheResult = CacheResults.CACHE_FAIL;
            }

            removeFilesThatStartWithFromDir(mContext.getCacheDir(), TRANSFER_PREFIX);

            return cacheResult;
        }

        /**
         * Returns a Uri with write permission to the requester
         *
         * @param packageName name of the package that should get permissions on the uri
         * @return Uri with write permission to the requester
         * @throws RemoteException When unable to create a file in internal storage of Aurora
         */
        @Override
        public Uri getWritePermissionUri(String packageName) throws RemoteException {

            Uri uri;

            try {
                File file = File.createTempFile(TRANSFER_PREFIX, EXTENSION, mContext.getCacheDir());

                uri = FileProvider.getUriForFile(mContext, "com.aurora.aurora.provider", file);

            } catch (IOException e) {
                Log.e(CLASS_TAG, "Unable to create a file in internal storage of Aurora", e);
                throw new RemoteException("Unable to create a file in internal storage of Aurora");
            }

            mContext.grantUriPermission(packageName, uri,
                    Intent.FLAG_GRANT_WRITE_URI_PERMISSION + Intent.FLAG_GRANT_READ_URI_PERMISSION);

            return uri;
        }

        /**
         * Returns the content of the file on uri as a String. The file should be in internal
         * storage of Aurora
         *
         * @param uri the uri on which the cached file is stored. This file is assumed to be in
         *            internal storage of Aurora
         * @return the content of the file on uri as a String
         * @throws IOException on file exceptions
         */
        private String readFile(Uri uri) throws IOException {
            // Open the file
            ParcelFileDescriptor inputPFD = mContext.getContentResolver().openFileDescriptor(uri
                    , "r");

            if (inputPFD == null) {
                throw new IllegalArgumentException("The file could not be opened");
            }

            // Read the file
            StringBuilder total = new StringBuilder();
            InputStream fileStream = new FileInputStream(inputPFD.getFileDescriptor());
            try (BufferedReader r = new BufferedReader(new InputStreamReader(fileStream))) {
                for (String line; (line = r.readLine()) != null; ) {
                    total.append(line).append('\n');
                }
            }

            return total.toString();
        }

        /**
         * Private non recursive helper function to clear a directory from files that start with prefix
         *
         * @param dir    the directory that needs files to be removed from
         * @param prefix the files that will be deleted start with this prefix
         */
        @SuppressWarnings("squid:S4042")
        // This warning is suppressed because it's not android compliant.
        private void removeFilesThatStartWithFromDir(File dir, String prefix) {
            if (dir != null && dir.isDirectory()) {
                File[] files = dir.listFiles();

                for (File file : files) {
                    if (file.getName().startsWith(prefix)) {
                        boolean success = file.delete();
                        if (!success) {
                            Log.e(CLASS_TAG, "There was a problem removing old files from "
                                    + dir.getName());
                            return;
                        }
                    }
                }
            }
        }

    }
}
