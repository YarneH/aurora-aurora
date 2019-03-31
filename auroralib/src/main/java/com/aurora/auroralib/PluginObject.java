package com.aurora.auroralib;

import com.google.gson.Gson;

import java.io.Serializable;
/**
 * Class to represent text processed by the plugin
 */
public abstract class PluginObject implements Serializable {
    static protected Gson mGson = new Gson();

    // TODO: check polymorphism
    /**
     * Turns the PLuginObject to a JSON string for easy caching.
     *
     * @return String (in JSON format)
     */
    public String toJSON(){
        return mGson.toJson(this);
    }

    /**
     * Turn the JSON string back into a PluginObject object.
     * This method needs to be overridden by subclasses
     *
     * @param json  The extracted JSON string of the PluginObject object
     * @return PluginObject
     */
    public static PluginObject fromJson(String json){
        return mGson.fromJson(json, PluginObject.class);
    }

}
