package com.aurora.plugin;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Class that maintains references to the environment and processor of plugin
 */
public class Plugin {

    /**
     * The set of default supported internal services when the user does not provide his own set
     */
    private static final List<InternalServices> DEFAULT_INTERNAL_SERVICES =
            new ArrayList<>(Collections.singletonList(InternalServices.TEXT_EXTRACTION));

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
    private Bitmap mPluginLogo;

    /**
     * A description of what the plugin is used for
     */
    private String mDescription;

    /**
     * A version number for the plugin.
     */
    private int mVersionCode;

    /**
     * A version code for the plugin, For example "v1.0" or "v2.2.3"
     */
    private String mVersionName;

    /**
     * The internal services needed by the plugin.
     */
    private List<InternalServices> mInternalServices;

    /**
     * Constructs a plugin metadata object
     *
     * @param uniqueName       the unique name of the plugin. Preferably the domain + version number
     * @param name             the display name of the plugin
     * @param pluginLogo       the logo of the plugin
     * @param description      the description of what the plugin is used for
     * @param versionCode      the version code for the plugin
     * @param versionName      the version name for the plugin
     * @param internalServices the internal services needed by the plugin
     */
    public Plugin(@NonNull String uniqueName, @NonNull String name, Bitmap pluginLogo, @NonNull String description,
                  int versionCode, @NonNull String versionName,
                  @NonNull List<InternalServices> internalServices) {
        mUniqueName = uniqueName;
        mName = name;
        mPluginLogo = pluginLogo;
        mDescription = description;
        mVersionCode = versionCode;
        mVersionName = versionName;
        mInternalServices = internalServices;
    }

    /**
     * Constructs a plugin metadata object with the default supported services
     *
     * @param uniqueName    the unique name of the plugin. Preferably the domain + version number
     * @param name          the display name of the plugin
     * @param pluginLogo    the logo of the plugin
     * @param description   the description of what the plugin is used for
     * @param versionCode   the version code for the plugin
     * @param versionName   the version name for the plugin
     */
    public Plugin(String uniqueName, String name, Bitmap pluginLogo, String description,
                  int versionCode, String versionName) {
        this(uniqueName, name, pluginLogo, description, versionCode, versionName, DEFAULT_INTERNAL_SERVICES);
    }

    /**
     * @return the unique name of the plugin
     */
    public String getUniqueName() {
        return mUniqueName;
    }

    /**
     * @return the display name of the plugin
     */
    public String getName() {
        return mName;
    }

    /**
     * @return the plugin logo, or null if no logo
     */
    public Bitmap getPluginLogo() {
        return mPluginLogo;
    }

    /**
     * @return a description of what the plugin is used for
     */
    public String getDescription() {
        return mDescription;
    }

    /**
     * @return the version number of the plugin. For example: 1,2,3,...
     */
    public int getVersionCode() {
        return mVersionCode;
    }

    /**
     * @return the version code of the plugin. For example: "v0.1", "v1.1.2", "v2.3.2-beta",...
     */
    public String getVersionName() {
        return mVersionName;
    }

    /**
     * @return the set of internal services that should be run on files processed by this plugin
     */
    public List<InternalServices> getInternalServices() {
        return mInternalServices;
    }

    /**
     * @return a default set of internal services in case the internal services were not explicitly set
     */
    public static List<InternalServices> getDefaultInternalServices() {
        return DEFAULT_INTERNAL_SERVICES;
    }
}
