package com.aurora.plugin;

/**
 * Class that maintains references to fragment and processor of plugin
 */
public abstract class Plugin {
    private PluginFragment mPluginFragment;
    private PluginProcessor mPluginProcessor;

    public abstract void loadPlugin();

    public abstract void loadSettingsActivity();
}
