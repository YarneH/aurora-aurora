package com.aurora.plugin;

import com.aurora.processingservice.PluginProcessor;

/**
 * Class that maintains references to fragment and processor of plugin
 */
public abstract class Plugin {
    private PluginFragment mPluginFragment;
    private PluginProcessor mPluginProcessor;

    public abstract void loadPlugin();

    public abstract void loadSettingsActivity();
}
