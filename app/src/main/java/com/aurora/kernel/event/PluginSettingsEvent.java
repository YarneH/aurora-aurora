package com.aurora.kernel.event;

/**
 * Event to request the settings activity of a plugin
 */
public class PluginSettingsEvent extends Event {

    private String mPluginName;

    public PluginSettingsEvent(String pluginName) {
        super();
        this.mPluginName = pluginName;
    }

    public String getPluginName() {
        return mPluginName;
    }
}
