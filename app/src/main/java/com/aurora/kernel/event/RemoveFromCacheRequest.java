package com.aurora.kernel.event;

import android.support.annotation.NonNull;

/**
 * This class is used to remove 1 or more files from the cache
 *
 * @see RemoveFromCacheResponse
 */
public class RemoveFromCacheRequest implements Event {
    /**
     * a reference to the file to be removed from the cache. If null, either all files from the specified plugin are
     * deleted, or if the plugin name is null too, clears the cache.
     * (should be hash_displayName). Check the getFileName method from MainActivity.
     */
    private String mFileRef;

    /**
     * The name of the plugin to remove the file from. If the fileref is null, all the files processed with this
     * plugin will be removed. If both fields are null, the cache is cleared completely
     */
    private String mUniquePluginName;

    /**
     * Creates a new RemoveFromCacheRequest to remove a particular file processed with a particular
     * plugin from the cache.
     *
     * @param fileRef          a reference to the original file of which the cached version should be removed
     *                         (should be hash_displayName). Check the getFileName method from MainActivity.
     * @param uniquePluginName the name of the plugin that this file was processed with
     */
    public RemoveFromCacheRequest(@NonNull String fileRef, @NonNull String uniquePluginName) {
        // Set both non null to ensure that no request with null plugin name and
        // non null file ref can be constructed
        mFileRef = fileRef;
        mUniquePluginName = uniquePluginName;
    }

    /**
     * Creates a new RemoveFromCacheRequest to remove all cached files processed with a particular plugin
     *
     * @param uniquePluginName the name of the plugin of which the cached files should be deleted
     */
    public RemoveFromCacheRequest(String uniquePluginName) {
        mFileRef = null;
        mUniquePluginName = uniquePluginName;
    }

    /**
     * Creates a new RemoveFromCacheRequest to remove all cached files from the cache
     */
    public RemoveFromCacheRequest() {
        mFileRef = null;
        mUniquePluginName = null;
    }

    /**
     * @return A reference to the file of which the cached version should be removed, or null if there is no particular
     * file to be removed, but multiple files
     */
    public String getFileRef() {
        return mFileRef;
    }

    /**
     * @return The name of the plugin of which one or all files should be removed from the cache, or null if the
     * entire cache has to be cleared
     */
    public String getUniquePluginName() {
        return mUniquePluginName;
    }

    /**
     * Method to check if the request wants to clear the entire cache at once
     *
     * @return true if the cache should be cleared completely
     */
    public boolean isClearCache() {
        return mFileRef == null && mUniquePluginName == null;
    }
}
