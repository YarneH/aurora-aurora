package com.aurora.kernel.event;

import android.content.Context;

/**
 * Event used to open an already cached file with a given plugin
 */
public class OpenCachedFileWithPluginRequest extends Event {
    private String mJsonRepresentation;
    private Context mContext;

    public OpenCachedFileWithPluginRequest(String jsonRepresentation, Context context) {
        mJsonRepresentation = jsonRepresentation;
        mContext = context;
    }

    public String getJsonRepresentation() {
        return mJsonRepresentation;
    }

    public Context getContext() {
        return mContext;
    }
}
