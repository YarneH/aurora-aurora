package com.aurora.plugin;

import com.aurora.externalservice.PluginEnvironment;
import com.aurora.processingservice.PluginProcessor;

import java.io.File;

/**
 * Class that maintains references to the environment and processor of plugin
 */
public class Plugin {

    protected String mUniqueName;
    protected String mDisplayName;
    protected File mPluginLogo;
    protected String mDescription;
    protected String mVersion;

    /**
     * Reference to the plugin's environment
     * Not serialized along with plugin
     */
    protected transient PluginEnvironment mPluginEnvironment;

    /**
     * Reference to the plugin's processor
     * Not serialized along with plugin
     */
    protected transient PluginProcessor mPluginProcessor;

    public Plugin(String uniqueName, String displayName, File pluginLogo, String description, String version,
                  PluginEnvironment pluginEnvironment, PluginProcessor pluginProcessor) {
        mUniqueName = uniqueName;
        mDisplayName = displayName;
        mPluginLogo = pluginLogo;
        mDescription = description;
        mVersion = version;
        mPluginEnvironment = pluginEnvironment;
        mPluginProcessor = pluginProcessor;
    }

    public String getUniqueName() {
        return mUniqueName;
    }

    public String getDisplayName() {
        return mDisplayName;
    }

    public File getPluginLogo() {
        return mPluginLogo;
    }

    public String getDescription() {
        return mDescription;
    }

    public String getVersion() {
        return mVersion;
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
        return new BasicPlugin(mDisplayName, mPluginLogo, mDescription, mVersion);
    }
}
