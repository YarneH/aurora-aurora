package com.aurora.kernel.event;

import com.aurora.plugin.ProcessedText;

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
    private ProcessedText mText;

    /**
     * The unique name of the plugin that built the representation of the text
     */
    private String mUniquePluginName;

    public CacheFileRequest(String fileRef, ProcessedText text, String uniquePluginName) {
        mFileRef = fileRef;
        mText = text;
        mUniquePluginName = uniquePluginName;
    }

    public String getFileRef() {
        return mFileRef;
    }

    public ProcessedText getText() {
        return mText;
    }

    public String getUniquePluginName() {
        return mUniquePluginName;
    }
}
