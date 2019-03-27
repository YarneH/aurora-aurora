package com.aurora.plugin;

import java.io.File;

/**
 * Class that maintains some basic information on a plugin
 */
public class BasicPlugin {
    private String mName;
    // TODO: Datatype may change
    private File mPluginLogo;
    private String mDescription;
    private String mVersion;

    public BasicPlugin(String name, File pluginLogo, String description, String version) {
        mName = name;
        mPluginLogo = pluginLogo;
        mDescription = description;
        mVersion = version;
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
