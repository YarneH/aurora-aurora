package com.aurora.kernel.event;

import android.content.Context;
import android.content.Intent;

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

    /**
     * Creates a new OpenFileWithPluginRequest
     *
     * @param extractedText the extracted text from the file to represent
     * @param pluginAction  the target intent of the chooser
     * @param chooser       the selected plugin
     * @param context       the android context
     */
    public OpenFileWithPluginRequest(ExtractedText extractedText, Intent pluginAction,
                                     Intent chooser, Context context) {
        mExtractedText = extractedText;
        mPluginAction = pluginAction;
        mChooser = chooser;
        mContext = context;
    }

    /**
     * @return the extracted text from the file to represent
     */
    public ExtractedText getExtractedText() {
        return mExtractedText;
    }

    /**
     * @return the target intent of the chooser
     */
    public Intent getPluginAction() {
        return mPluginAction;
    }

    /**
     * @return the selected plugin
     */
    public Intent getChooser() {
        return mChooser;
    }

    /**
     * @return the android context
     */
    public Context getContext() {
        return mContext;
    }

}
