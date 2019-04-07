package com.aurora.auroralib;

import com.google.gson.Gson;

import java.io.Serializable;
/**
 * Class to represent text processed by the plugin
 */
public abstract class PluginObject implements Serializable {
    /**
     * Gson object for turning PluginObject to Json string
     */
    protected static Gson sGson = new Gson();

    // TODO: check polymorphism
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
     * This method needs to be overridden by subclasses
     *
     * @param json  The extracted JSON string of the PluginObject object
     * @return PluginObject
     */
    public static final <T extends PluginObject> T fromJson(String json, Class<T> type){
        return sGson.fromJson(json, type);
    }

}
