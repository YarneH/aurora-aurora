package com.aurora.kernel.event;

import android.support.annotation.NonNull;

/**
 * This class is used to remove 1 or more files from the cache
 */
public class RemoveFromCacheRequest extends Event {
    /**
     * a reference to the file to be removed from the cache. If null, either all files from the specified plugin are
     * deleted, or if the plugin name is null too, clears the cache.
     */
    private String mFileRef;

    /**
     * The name of the plugin to remove the file from. If the fileref is null, all the files processed with this
     * plugin will be removed. If both fields are null, the cache is cleared completely
     */
    private String mUniquePluginName;

    public RemoveFromCacheRequest(@NonNull String fileRef, @NonNull String uniquePluginName) {
        // Set both non null to ensure that no request with null plugin name and
        // non null file ref can be constructed
        mFileRef = fileRef;
        mUniquePluginName = uniquePluginName;
    }

    public RemoveFromCacheRequest(String uniquePluginName) {
        mFileRef = null;
        mUniquePluginName = uniquePluginName;
    }

    public RemoveFromCacheRequest() {
        mFileRef = null;
        mUniquePluginName = null;
    }

    public String getFileRef() {
        return mFileRef;
    }

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
