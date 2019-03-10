package com.aurora.plugin;

import com.aurora.externalservice.PluginEnvironment;
import com.aurora.processingservice.PluginProcessor;

import java.io.File;

/**
 * Class that maintains references to the environment and processor of plugin
 */
public abstract class Plugin {

    protected String mName;
    protected File mPluginLogo;
    protected String mDescription;
    protected String mVersion;

    /**
     * Reference to the plugin's environment
     */
    protected PluginEnvironment mPluginEnvironment;

    /**
     * Reference to the plugin's processor
     */
    protected PluginProcessor mPluginProcessor;

    public Plugin(String name, File pluginLogo, String description, String version,
                  PluginEnvironment pluginEnvironment, PluginProcessor pluginProcessor) {
        mName = name;
        mPluginLogo = pluginLogo;
        mDescription = description;
        mVersion = version;
        mPluginEnvironment = pluginEnvironment;
        mPluginProcessor = pluginProcessor;
    }

    public PluginEnvironment getPluginEnvironment() {
        return mPluginEnvironment;
    }


    public PluginProcessor getPluginProcessor() {
        return mPluginProcessor;
    }

    /**
     * Takes a plugin and only returns the basic information as a BasicPlugin type
     *
     * @return the Basic plugin information
     */
    public BasicPlugin getBasicPluginInfo() {
        return new BasicPlugin(mName, mPluginLogo, mDescription, mVersion);
    }
}
