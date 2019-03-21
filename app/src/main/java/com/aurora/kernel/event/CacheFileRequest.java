package com.aurora.kernel.event;

import com.aurora.auroralib.PluginObject;

/**
 * A class that can be used to request that a certain file representation is cached
 */
public class CacheFileRequest extends Event {
    /**
     * A reference to the file that needs to be cached
     */
    private String mFileRef;

    /**
     * The processed text to be cached
     */
    private PluginObject mText;

    /**
     * The unique name of the plugin that built the representation of the text
     */
    private String mUniquePluginName;

    public CacheFileRequest(String fileRef, PluginObject text, String uniquePluginName) {
        mFileRef = fileRef;
        mText = text;
        mUniquePluginName = uniquePluginName;
    }

    public String getFileRef() {
        return mFileRef;
    }

    public PluginObject getText() {
        return mText;
    }

    public String getUniquePluginName() {
        return mUniquePluginName;
    }
}
