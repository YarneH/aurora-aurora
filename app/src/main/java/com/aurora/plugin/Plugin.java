package com.aurora.plugin;

import com.aurora.externalservice.PluginEnvironment;
import com.aurora.processingservice.PluginProcessor;

/**
 * Class that maintains references to the environment and processor of plugin
 */
public abstract class Plugin {
    /**
     * Reference to the plugin's environment
     */
    protected PluginEnvironment mPluginEnvironment;

    /**
     * Reference to the plugin's processor
     */
    protected PluginProcessor mPluginProcessor;

    public Plugin(PluginEnvironment pluginEnvironment, PluginProcessor pluginProcessor) {
        mPluginEnvironment = pluginEnvironment;
        mPluginProcessor = pluginProcessor;
    }

    public PluginEnvironment getPluginEnvironment() {
        return mPluginEnvironment;
    }


    public PluginProcessor getPluginProcessor() {
        return mPluginProcessor;
    }
}
