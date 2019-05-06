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
     * The unique name of the plugin that the file was processed with
     */
    private String mUniquePluginName;

    /**
     * A reference to the android context
     */
    private Context mContext;

    /**
     * Creates a new OpenCachedFileWithPluginsRequest
     *
     * @param jsonRepresentation a json representation of the processed file, in String format
     * @param uniquePluginName   the unique name of the plugin that the file was processed with.
     * @param context            a reference to the android context
     */
    public OpenCachedFileWithPluginRequest(String jsonRepresentation, String uniquePluginName, Context context) {
        mJsonRepresentation = jsonRepresentation;
        mUniquePluginName = uniquePluginName;
        mContext = context;
    }

    /**
     * @return a json representation of the processed file, in String format
     */
    public String getJsonRepresentation() {
        return mJsonRepresentation;
    }

    /**
     * @return the (unique) name of the plugin that the file was processed with
     */
    public String getUniquePluginName() {
        return mUniquePluginName;
    }

    /**
     * @return a reference to the android context
     */
    public Context getContext() {
        return mContext;
    }
}
