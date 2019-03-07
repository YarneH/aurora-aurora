package com.aurora.plugin;

import java.io.File;

/**
 * Class that maintains some basic information on a plugin
 */
public class BasicPlugin {
    private String mName;
    // TODO: Datatype may change
    private File mPluginLogo;

    public BasicPlugin(String name, File pluginLogo) {
        mName = name;
        mPluginLogo = pluginLogo;
    }

    public String getName() {
        return mName;
    }

    public File getPluginLogo() {
        return mPluginLogo;
    }
}
