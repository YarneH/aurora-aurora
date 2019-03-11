package com.aurora.kernel;

import android.util.Log;

import com.aurora.externalservice.PluginEnvironment;
import com.aurora.plugin.BasicPlugin;
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

/**
 * Class that maintains link between a plugin name and plugin
 */
class PluginRegistry {
    /**
     * Maintains a relation between a plugin name and a Plugin object
     */
    private Map<String, Plugin> mPluginsMap;

    /**
     * A reference to where the plugin config file can be found
     */
    private String mConfigFileRef;

    /**
     * A reference to the processing communicator
     */
    private ProcessingCommunicator mProcessingCommunicator;


    PluginRegistry(ProcessingCommunicator processingCommunicator, String configFileRef) {
        this.mProcessingCommunicator = processingCommunicator;

        this.mConfigFileRef = configFileRef;

        // Load plugins
        constructPluginMap();
    }


    /**
     * private helper method to find a plugin based on the plugin name
     *
     * @param pluginName the name of the plugin
     * @return the plugin object associated with the name or null if not found
     */
    private Plugin resolvePlugin(String pluginName) {
        // Returns the plugin if found, null otherwise
        return mPluginsMap.get(pluginName);
    }


    /**
     * Finds the PluginEnvironment to load given a pluginName
     *
     * @param pluginName the name of the plugin to load
     * @return the PluginEnvironment associated with the plugin name or null if not found
     */
    PluginEnvironment loadPlugin(String pluginName) {
        Plugin plugin = resolvePlugin(pluginName);

        if (plugin != null) {
            // Set plugin processor in the processing communicator
            mProcessingCommunicator.setActivePluginProcessor(plugin.getPluginProcessor());

            // Return the environment to the caller
            return plugin.getPluginEnvironment();
        } else {
            Log.d("PluginRegistry", "Could not find the plugin with name " +
                    pluginName + ".");

            return null;
        }
    }

    /**
     * Gets a list of all installed plugins
     *
     * @return List of Plugin objects with basic information
     */
    List<BasicPlugin> getPlugins() {
        List<BasicPlugin> basicPlugins = new ArrayList<>();

        // Loop over all values and extract their basic info
        for (Plugin p : mPluginsMap.values()) {
            // Create basic plugin
            basicPlugins.add(p.getBasicPluginInfo());
        }

        return basicPlugins;
    }

    /**
     * Adds a plugin with a given name to the map and writes back the configuration file
     *
     * @param pluginName the name of the plugin to add
     * @param plugin     the plugin object that contains the plugin
     * @return true if the plugin was added, false if the plugin could not be added (e.g. if it was already present)
     */
    boolean registerPlugin(String pluginName, Plugin plugin) {
        // TODO: write back config file immediately
        if (!mPluginsMap.containsKey(pluginName)) {
            // Add plugin to the map
            mPluginsMap.put(pluginName, plugin);
            persistPluginsMap();
            return true;
        }

        // Return false because plugin was already present
        return false;
    }

    /**
     * Removes a plugin with a given name if possible
     *
     * @param pluginName the name of the plugin to remove from the registry
     */
    void removePlugin(String pluginName) {
        mPluginsMap.remove(pluginName);
        persistPluginsMap();
    }

    /**
     * Removes all plugins from the registry
     */
    void removeAllPlugins() {
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
                // TODO: add appropriate pluginenvironment and processor to the plugin
                // Add the plugin to the map
                mPluginsMap.put(p.getUniqueName(), p);
            }
        } catch (IOException e) {
            Log.e("PluginRegistry", "Something went wrong when reading the plugins from the config file.");
        }

    }

    /**
     * Reads the file at a given location and attempts to parse it as a JSON array
     *
     * @return a JSONArray containing the various JSON objects representing plugins
     * @throws IOException when the file does not exist or something went wrong during the file read
     */
    private String parsePluginFile() throws IOException {
        // Get file at specified path
        File pluginConfig = new File(mConfigFileRef);

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

        // Write to config file+
        try (Writer writer = new BufferedWriter(new FileWriter(mConfigFileRef))) {
            writer.write(pluginsJson);
            writer.flush();
        } catch (IOException e) {
            Log.e("PluginRegistry", "Could not write to plugins config file.");
            e.printStackTrace();
        }
    }
}
