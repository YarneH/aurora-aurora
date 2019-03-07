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

    public BasicPlugin(String name, File pluginLogo, String description) {
        mName = name;
        mPluginLogo = pluginLogo;
        mDescription = description;
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
}
