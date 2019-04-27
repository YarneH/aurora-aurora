package com.aurora.auroralib;

import com.google.gson.Gson;

import java.io.Serializable;
/**
 * Class to represent text processed by the plugin
 */
public abstract class PluginObject implements Serializable {

    /**
     * The name of the file that contained the text that is now displayed in the PluginObject
     */
    protected String mFileName;

    /**
     * The name of the plugin that the file was processed with
     */
    protected String mUniquePluginName;

    /**
     * Gson object for turning PluginObject to Json string
     */
    protected static Gson sGson = new Gson();

    public PluginObject(String fileName, String uniquePluginName) {
        mFileName = fileName;
        mUniquePluginName = uniquePluginName;
    }

    /**
     * Turns the PLuginObject to a JSON string for easy caching.
     *
     * @return String (in JSON format)
     */
    public String toJSON(){
        return sGson.toJson(this);
    }

    /**
     * Turn the JSON string back into a PluginObject object.
     *
     * @param json  The extracted JSON string of the PluginObject object
     * @return PluginObject
     */
    public static final <T extends PluginObject> T fromJson(String json, Class<T> type){
        return sGson.fromJson(json, type);
    }

}
