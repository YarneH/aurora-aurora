package com.aurora.kernel.event;

import com.aurora.auroralib.PluginObject;

/**
 * A class that can be used to request that a certain file representation is cached
 * @see CacheFileResponse
 */
public class CacheFileRequest extends Event {
    /**
     * A reference to the file that needs to be cached
     */
    private String mFileRef;

    /**
     * The processed text to be cached
     */
    private PluginObject mPluginObject;

    /**
     * The unique name of the plugin that built the representation of the text
     */
    private String mUniquePluginName;

    /**
     * Creates a new CacheFileRequest
     *
     * @param fileRef          a reference to the original file that is to be cached
     * @param pluginObject     a pluginobject containing the processed contents of the file
     * @param uniquePluginName the name of the plugin that the file was processed with
     */
    public CacheFileRequest(String fileRef, PluginObject pluginObject, String uniquePluginName) {
        mFileRef = fileRef;
        mPluginObject = pluginObject;
        mUniquePluginName = uniquePluginName;
    }

    /**
     * @return a reference to the original file that is to be cached
     */
    public String getFileRef() {
        return mFileRef;
    }

    /**
     * @return a pluginobject containing the processed contents of the file
     */
    public PluginObject getPluginObject() {
        return mPluginObject;
    }

    /**
     * @return the name of the plugin that the file was processed with
     */
    public String getUniquePluginName() {
        return mUniquePluginName;
    }
}
