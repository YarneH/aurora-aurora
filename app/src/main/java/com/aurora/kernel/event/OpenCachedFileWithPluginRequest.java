package com.aurora.kernel.event;

import android.content.Context;

/**
 * Event used to open an already cached file with a given plugin
 */
public class OpenCachedFileWithPluginRequest implements Event {
    /**
     * A json representation of the processed file, in String format
     */
    private String mJsonRepresentation;

    /**
     * A reference to the android context
     */
    private Context mContext;

    /**
     * Creates a new OpenCachedFileWithPluginsRequest
     *
     * @param jsonRepresentation a json representation of the processed file, in String format
     * @param context            a reference to the android context
     */
    public OpenCachedFileWithPluginRequest(String jsonRepresentation, Context context) {
        mJsonRepresentation = jsonRepresentation;
        mContext = context;
    }

    /**
     * @return a json representation of the processed file, in String format
     */
    public String getJsonRepresentation() {
        return mJsonRepresentation;
    }

    /**
     * @return a reference to the android context
     */
    public Context getContext() {
        return mContext;
    }
}
