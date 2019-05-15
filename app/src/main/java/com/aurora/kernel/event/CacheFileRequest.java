package com.aurora.kernel.event;

/**
 * A class that can be used to request that a certain file representation is cached
 *
 * @see CacheFileResponse
 */
public class CacheFileRequest implements Event {
    /**
     * A reference to the file that needs to be cached (should be hash_displayName)
     * Check the getFileName method from MainActivity.
     */
    private String mFileRef;

    /**
     * The processed text to be cached
     */
    private String mPluginObjectJson;

    /**
     * The unique name of the plugin that built the representation of the text
     */
    private String mUniquePluginName;

    /**
     * Creates a new CacheFileRequest
     *
     * @param fileRef          a reference to the original file that is to be cached (should be hash_displayName)
     *                         Check the getFileName method from MainActivity.
     * @param pluginObject     a json representation containing the processed contents of the file
     * @param uniquePluginName the name of the plugin that the file was processed with
     */
    public CacheFileRequest(String fileRef, String pluginObject, String uniquePluginName) {
        mFileRef = fileRef;
        mPluginObjectJson = pluginObject;
        mUniquePluginName = uniquePluginName;
    }

    /**
     * @return a reference to the original file that is to be cached
     */
    public String getFileRef() {
        return mFileRef;
    }

    /**
     * @return a json representation containing the processed contents of the file
     */
    public String getPluginObject() {
        return mPluginObjectJson;
    }

    /**
     * @return the name of the plugin that the file was processed with
     */
    public String getUniquePluginName() {
        return mUniquePluginName;
    }
}
