package com.aurora.kernel.event;

import android.content.Context;
import android.content.Intent;

import com.aurora.auroralib.ExtractedText;

/**
 * Event to request that a file is opened with a plugin
 */
public class OpenFileWithPluginRequest extends Event {
    /**
     * The extracted text from the file to represent
     */
    private ExtractedText mExtractedText;

    /**
     * The plugin to open the file with
     */
    private Intent mTargetPlugin;

    /**
     * The android context
     */
    private Context mContext;

    public OpenFileWithPluginRequest(ExtractedText extractedText, Intent targetPlugin, Context context) {
        mExtractedText = extractedText;
        mTargetPlugin = targetPlugin;
        mContext = context;
    }

    public ExtractedText getExtractedText() {
        return mExtractedText;
    }

    public Intent getTargetPlugin() {
        return mTargetPlugin;
    }

    public Context getContext() {
        return mContext;
    }

}
