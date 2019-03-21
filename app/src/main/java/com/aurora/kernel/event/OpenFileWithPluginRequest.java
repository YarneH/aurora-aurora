package com.aurora.kernel.event;

import com.aurora.internalservice.internalprocessor.ExtractedText;

/**
 * Event to request that a file is opened with a plugin
 */
public class OpenFileWithPluginRequest extends Event {
    /**
     * The extracted text from the file to represent
     */
    private ExtractedText mExtractedText;

    /**
     * The name of the plugin to open the file with
     */
    private String mPluginName;

    public OpenFileWithPluginRequest(ExtractedText extractedText, String pluginName) {
        mExtractedText = extractedText;
        mPluginName = pluginName;
    }

    public ExtractedText getExtractedText() {
        return mExtractedText;
    }

    public String getPluginName() {
        return mPluginName;
    }
}
