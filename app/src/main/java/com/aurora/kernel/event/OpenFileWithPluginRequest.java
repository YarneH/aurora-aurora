package com.aurora.kernel.event;

import android.content.Context;

import com.aurora.auroralib.ExtractedText;

/**
 * Event to request that a file is opened with a plugin
 */
public class OpenFileWithPluginRequest implements Event {
    /**
     * The extracted text from the file to represent
     */
    private ExtractedText mExtractedText;

    /**
     * The (unique) name of the plugin to open the file with
     */
    private String mUniquePluginName;

    /**
     * The android context
     */
    private Context mContext;

    /**
     * Creates a new OpenFileWithPluginRequest
     *
     * @param extractedText the extracted text from the file to represent
     * @param pluginAction  the target intent of the chooser
     * @param chooser       the selected plugin
     * @param context       the android context
     */
    public OpenFileWithPluginRequest(ExtractedText extractedText, String uniquePluginName, Context context) {
        mExtractedText = extractedText;
        mUniquePluginName = uniquePluginName;
        mContext = context;
    }

    /**
     * @return the extracted text from the file to represent
     */
    public ExtractedText getExtractedText() {
        return mExtractedText;
    }

    /**
     * @return the unique name of the plugin to open the file with
     */
    public String getUniquePluginName() {
        return mUniquePluginName;
    }

    /**
     * @return the android context
     */
    public Context getContext() {
        return mContext;
    }

}
