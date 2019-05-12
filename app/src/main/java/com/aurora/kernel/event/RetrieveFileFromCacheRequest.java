package com.aurora.kernel.event;

/**
 * This class is used to actually retrieve a file from the cache (not just basic info as QueryCacheRequest).
 *
 * @see RetrieveFileFromCacheResponse
 */
public class RetrieveFileFromCacheRequest implements Event {
    /**
     * A reference to the file to retrieve the representation from (should be hash_displayName)
     * Check the getFileName method from MainActivity.
     */
    private String mFileRef;

    /**
     * The name of the plugin that the file was processed with
     */
    private String mUniquePluginName;

    /**
     * Creates a new RetrieveFileFromCacheRequest
     *
     * @param fileRef          a reference to the original file of which the cached version should be
     *                         retrieved from the cache (should be hash_displayName)
     *                         Check the getFileName method from MainActivity.
     * @param uniquePluginName the name of the plugin that the file was processed with
     */
    public RetrieveFileFromCacheRequest(String fileRef, String uniquePluginName) {
        mFileRef = fileRef;
        mUniquePluginName = uniquePluginName;
    }

    /**
     * @return a reference to the original file which the cached version should be retrieved from the cache
     */
    public String getFileRef() {
        return mFileRef;
    }

    /**
     * @return the name of the plugin that the file was processed with
     */
    public String getUniquePluginName() {
        return mUniquePluginName;
    }
}
