package com.aurora.plugin;

import java.io.File;

/**
 * Class that maintains references to the environment and processor of plugin
 */
public abstract class Plugin {

    protected String mName;
    protected File mPluginLogo;
    protected String mDescription;
    protected String mVersion;

    public Plugin(String name, File pluginLogo, String description, String version) {
        mName = name;
        mPluginLogo = pluginLogo;
        mDescription = description;
        mVersion = version;
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
