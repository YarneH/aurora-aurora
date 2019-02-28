package com.aurora.kernel.event;

/**
 * Event to request the settings activity of a plugin
 */
public class PluginSettingsRequest extends Event {

    private String mPluginName;

    public PluginSettingsRequest(String pluginName) {
        super();
        this.mPluginName = pluginName;
    }

    public String getPluginName() {
        return mPluginName;
    }
}
