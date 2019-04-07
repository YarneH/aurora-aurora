package com.aurora.kernel.event;

import android.content.Context;

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
     * The android context
     */
    private Context mContext;

    public OpenFileWithPluginRequest(ExtractedText extractedText, Context context) {
        mExtractedText = extractedText;
        mContext = context;
    }

    public ExtractedText getExtractedText() {
        return mExtractedText;
    }

    public Context getContext() {
        return mContext;
    }

}
