package com.aurora.kernel.event;

/**
 * This event class is used for querying the cache.
 * There are two uses. Either, this request is used to query for one specific file.
 * Another possibility is to query the cache for all files that it has stored.
 *
 * @see QueryCacheResponse
 */
public class QueryCacheRequest implements Event {

    /**
     * The maximum number of entries that should be retrieved.
     * This entry will not be used when a specific file is queried
     */
    private int mMaxEntries;

    /**
     * A reference to the file to be queried (if there is a specific file. Null if cache is queried for all files
     * (should be hash_displayName). Check the getFileName method from MainActivity.
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
        this(0);
    }

    /**
     * Creates a new (empty) QueryCacheRequest to get a number of files in the cache
     *
     * @param maxEntries the maxmimum number of entries that should be returned. If this is <= 0, it will return
     *                   all the files instead.
     */
    public QueryCacheRequest(int maxEntries) {
        this(null, null);
        mMaxEntries = maxEntries;
    }

    /**
     * Creates a new QueryCacheRequest to get a specific file from the cache
     *
     * @param fileRef          a reference to the file to be queried (should be hash_displayName)
     *                         Check the getFileName method from MainActivity.
     * @param uniquePluginName the name of the plugin that the cached file was processed with
     */
    public QueryCacheRequest(String fileRef, String uniquePluginName) {
        mFileRef = fileRef;
        mUniquePluginName = uniquePluginName;
    }

    /**
     * @return the maximum number of entries that should be retrieved
     */
    public int getMaxEntries() {
        return mMaxEntries;
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
