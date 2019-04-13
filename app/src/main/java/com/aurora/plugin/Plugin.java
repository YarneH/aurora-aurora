package com.aurora.plugin;

import android.support.annotation.NonNull;

import java.io.File;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Class that maintains references to the environment and processor of plugin
 */
public class Plugin {

    /**
     * The set of default supported internal services when the user does not provide his own set
     */
    private static final Set<InternalServices> DEFAULT_INTERNAL_SERVICES =
            new HashSet<>(Collections.singletonList(InternalServices.TEXT_EXTRACTION));

    /**
     * The unique name for the plugin.
     * For example, "com.aurora.souschef0.1"
     */
    private String mUniqueName;

    /**
     * The display name used for the plugin.
     * For example, "Souschef"
     */
    private String mName;

    /**
     * A logo for the plugin
     */
    private File mPluginLogo;

    /**
     * A description of what the plugin is used for
     */
    private String mDescription;

    /**
     * A version number for the plugin.
     */
    private int mVersionNumber;

    /**
     * A version code for the plugin, For example "v1.0" or "v2.2.3"
     */
    private String mVersionCode;

    /**
     * The internal services needed by the plugin.
     */
    private Set<InternalServices> mInternalServices;

    /**
     * Constructs a plugin metadata object
     *
     * @param uniqueName       the unique name of the plugin. Preferably the domain + version number
     * @param name             the display name of the plugin
     * @param pluginLogo       the logo of the plugin
     * @param description      the description of what the plugin is used for
     * @param versionNumber    the version number for the plugin
     * @param versionCode      the version code for the plugin
     * @param internalServices the internal services needed by the plugin
     */
    public Plugin(@NonNull String uniqueName, @NonNull String name, File pluginLogo, @NonNull String description,
                  int versionNumber, @NonNull String versionCode, @NonNull Set<InternalServices> internalServices) {
        mUniqueName = uniqueName;
        mName = name;
        mPluginLogo = pluginLogo;
        mDescription = description;
        mVersionNumber = versionNumber;
        mVersionCode = versionCode;
        mInternalServices = internalServices;
    }

    /**
     * Constructs a plugin metadata object with the default supported services
     *
     * @param uniqueName    the unique name of the plugin. Preferably the domain + version number
     * @param name          the display name of the plugin
     * @param pluginLogo    the logo of the plugin
     * @param description   the description of what the plugin is used for
     * @param versionNumber the version number for the plugin
     * @param versionCode   the version code for the plugin
     */
    public Plugin(String uniqueName, String name, File pluginLogo, String description,
                  int versionNumber, String versionCode) {
        this(uniqueName, name, pluginLogo, description, versionNumber, versionCode, DEFAULT_INTERNAL_SERVICES);
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

    public int getVersionNumber() {
        return mVersionNumber;
    }

    public String getVersionCode() {
        return mVersionCode;
    }

    public Set<InternalServices> getInternalServices() {
        return mInternalServices;
    }

    public static Set<InternalServices> getDefaultInternalServices() {
        return DEFAULT_INTERNAL_SERVICES;
    }
}
