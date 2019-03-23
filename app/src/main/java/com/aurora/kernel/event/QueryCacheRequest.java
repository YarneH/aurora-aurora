package com.aurora.kernel.event;

/**
 * This event class is used for querying the cache.
 * There are two uses. Either, this request is used to query for one specific file.
 * Another possibility is to query the cache for all files that it has stored.
 */
public class QueryCacheRequest extends Event {
    /**
     * A reference to the file to be queried (if there is a specific file. Null if cache is queried for all files
     */
    private String mFileRef;

    /**
     * The name of the plugin that the cached file was processed with.
     * If the fileRef is null, this parameter will be ignored
     */
    private String mUniquePluginName;

    public QueryCacheRequest() {
        this(null, null);
    }

    public QueryCacheRequest(String fileRef, String uniquePluginName) {
        mFileRef = fileRef;
        mUniquePluginName = uniquePluginName;
    }

    public String getFileRef() {
        return mFileRef;
    }

    public String getUniquePluginName() {
        return mUniquePluginName;
    }

    /**
     * Method to check if entire cache should be returned
     *
     * @return true if the entire cache should be returned, false otherwise
     */
    public boolean isFullCacheRequest() {
        return mFileRef == null;
    }
}
