package com.aurora.plugin;

import java.io.File;

/**
 * Class that maintains references to the environment and processor of plugin
 */
public class Plugin {

    private String mUniqueName;
    private String mName;
    private File mPluginLogo;
    private String mDescription;
    private String mVersion;

    public Plugin(String uniqueName, String name, File pluginLogo, String description, String version) {
        mUniqueName = uniqueName;
        mName = name;
        mPluginLogo = pluginLogo;
        mDescription = description;
        mVersion = version;
    }

    public String getUniqueName() {
        return mUniqueName;
    }

    public String getName() {
        return mName;
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
}
