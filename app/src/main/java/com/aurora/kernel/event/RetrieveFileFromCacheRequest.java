package com.aurora.kernel.event;

/**
 * This class is used to actually retrieve a file from the cache (not just basic info as QueryCacheRequest).
 */
public class RetrieveFileFromCacheRequest extends Event {
    /**
     * A reference to the file to retrieve the representation from
     */
    private String mFileRef;

    /**
     * The name of the plugin that the file was processed with
     */
    private String mUniquePluginName;

    public RetrieveFileFromCacheRequest(String fileRef, String uniquePluginName) {
        mFileRef = fileRef;
        mUniquePluginName = uniquePluginName;
    }

    public String getFileRef() {
        return mFileRef;
    }

    public String getUniquePluginName() {
        return mUniquePluginName;
    }
}
