package com.aurora.kernel;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.aurora.plugin.Plugin;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Class that maintains link between a plugin name and plugin
 */
class PluginRegistry {
    private static final String PLUGINREGISTRY_LOG_TAG = "PluginRegistry";
    /**
     * Maintains a relation between a plugin name and a Plugin object
     */
    private Map<String, Plugin> mPluginsMap;

    /**
     * A reference to where the plugin config file can be found
     */
    private String mConfigFileRef;

    /**
     * The android context
     */
    private Context mContext;

    /**
     * Creates a new PluginRegistry. There should be only one instance at a time
     * @param configFileRef a string containing the path to the config file of the registry
     * @param context a reference to the android context, necessary for file IO
     */
    PluginRegistry(@NonNull final String configFileRef, @NonNull final Context context) {

        this.mConfigFileRef = configFileRef;

        this.mContext = context;

        // Load plugins
        constructPluginMap();
    }

    /**
     * Returns metadata of the selected plugin
     *
     * @param pluginName the name of the plugin to load
     * @return the Plugin associated with the plugin name or null if not found
     */
    public @Nullable Plugin getPlugin(@NonNull final String pluginName) {
        return mPluginsMap.get(pluginName);
    }

    /**
     * Gets a list of all installed plugins
     *
     * @return List of Plugin objects with basic information
     */
    public @NonNull List<Plugin> getPlugins() {
        // Create list from the values
        return new ArrayList<>(mPluginsMap.values());
    }

    /**
     * Adds a plugin with a given name to the map and writes back the configuration file
     *
     * @param pluginName          the name of the plugin to add
     * @param plugin              the plugin object that contains the plugin
     * @param overwriteOldVersion indicates whether an old version of the plugin should be overwritten
     *                            with a new version (compares the version numbers)
     * @return true if the plugin was added, false if the plugin could not be added (e.g. if it was already present)
     */
    public boolean registerPlugin(@NonNull final String pluginName, @NonNull final Plugin plugin,
                                  final boolean overwriteOldVersion) {
        if (!mPluginsMap.containsKey(pluginName) || (overwriteOldVersion &&
                Objects.requireNonNull(mPluginsMap.get(pluginName)).getVersionCode() < plugin.getVersionCode())) {

            // Add plugin to the map
            mPluginsMap.put(pluginName, plugin);

            // Persist plugin
            persistPluginsMap();

            return true;
        }

        // Return false because plugin was already present
        return false;
    }

    /**
     * Adds a plugin with a given name to the map and writes back the configuration file.
     * Only adds the plugin if there is no previous version of it in the registry.
     *
     * @param pluginName          the name of the plugin to add
     * @param plugin              the plugin object that contains the plugin
     * @return true if the plugin was added, false if the plugin could not be added (e.g. if it was already present)
     */
    public boolean registerPlugin(@NonNull final String pluginName, @NonNull final Plugin plugin) {
        return this.registerPlugin(pluginName, plugin, false);
    }

    /**
     * Removes a plugin with a given name if possible
     *
     * @param pluginName the name of the plugin to remove from the registry
     */
    public void removePlugin(@NonNull final String pluginName) {
        mPluginsMap.remove(pluginName);
        persistPluginsMap();
    }

    /**
     * Removes all plugins from the registry
     */
    public void removeAllPlugins() {
        mPluginsMap.clear();
        persistPluginsMap();
    }

    /**
     * Private helper method that reads the available plugins from a config file
     */
    private void constructPluginMap() {
        // Create map
        mPluginsMap = new HashMap<>();

        try {
            // Read file
            String pluginsJson = parsePluginFile();

            // Read file as JSON
            Gson gson = new Gson();
            Plugin[] registeredPlugins = gson.fromJson(pluginsJson, Plugin[].class);

            for (Plugin p : registeredPlugins) {
                // Add the plugin to the map
                mPluginsMap.put(p.getUniqueName(), p);
            }
        } catch (IOException e) {
            Log.e(PLUGINREGISTRY_LOG_TAG, "Something went wrong when reading the plugins from the config file.");
        }

    }

    /**
     * Reads the file at a given location and attempts to parse it as a JSON array
     *
     * @return a JSONArray containing the various JSON objects representing plugins
     * @throws IOException when the file does not exist or something went wrong during the file read
     */
    private @NonNull String parsePluginFile() throws IOException {
        // Get file at specified path
        File pluginConfig = new File(mContext.getFilesDir(), mConfigFileRef);

        StringBuilder stringBuilder;
        try (BufferedReader reader = new BufferedReader(new FileReader(pluginConfig))) {
            stringBuilder = new StringBuilder();
            String currentLine;

            while ((currentLine = reader.readLine()) != null) {
                stringBuilder.append(currentLine);
            }
        }

        return stringBuilder.toString();
    }

    /**
     * Helper method that persists any changes to the plugin config
     */
    private void persistPluginsMap() {
        // Get the values of the map into an array
        Plugin[] plugins = mPluginsMap.values().toArray(new Plugin[0]);

        // Serialize to json
        Gson gson = new Gson();
        String pluginsJson = gson.toJson(plugins);

        // Write to config file
        File configFile = new File(mContext.getFilesDir(), mConfigFileRef);
        try (Writer writer = new BufferedWriter(new FileWriter(configFile))) {
            writer.write(pluginsJson);
            writer.flush();
        } catch (IOException e) {
            Log.e(PLUGINREGISTRY_LOG_TAG, "Could not write to plugins config file.");
        }
    }
}
