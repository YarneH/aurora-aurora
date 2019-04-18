package com.aurora.kernel.event;

/**
 * This event class is used for querying the cache.
 * There are two uses. Either, this request is used to query for one specific file.
 * Another possibility is to query the cache for all files that it has stored.
 * @see QueryCacheResponse
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

    /**
     * Creates a new (empty) QueryCacheRequest to get all files in the cache
     */
    public QueryCacheRequest() {
        this(null, null);
    }

    /**
     * Creates a new QueryCacheRequest to get a specific file from the cache
     *
     * @param fileRef          a reference to the file to be queried
     * @param uniquePluginName the name of the plugin that the cached file was processed with
     */
    public QueryCacheRequest(String fileRef, String uniquePluginName) {
        mFileRef = fileRef;
        mUniquePluginName = uniquePluginName;
    }

    /**
     * @return a reference to the file to be queried
     */
    public String getFileRef() {
        return mFileRef;
    }

    /**
     * @return the name of the plugin that the cached file was processed with
     */
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
