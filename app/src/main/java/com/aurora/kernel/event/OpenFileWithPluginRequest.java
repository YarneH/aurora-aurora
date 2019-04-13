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
     * The target intent of the chooser
     */
    private Intent mPluginAction;

    /**
     * The selected plugin
     */
    private Intent mChooser;

    /**
     * The android context
     */
    private Context mContext;

    public OpenFileWithPluginRequest(ExtractedText extractedText, Intent pluginAction, Intent chooser, Context context) {
        mExtractedText = extractedText;
        mPluginAction = pluginAction;
        mChooser = chooser;
        mContext = context;
    }

    public ExtractedText getExtractedText() {
        return mExtractedText;
    }

    public Intent getPluginAction() {
        return mPluginAction;
    }

    public Intent getChooser() {
        return mChooser;
    }

    public Context getContext() {
        return mContext;
    }

}
