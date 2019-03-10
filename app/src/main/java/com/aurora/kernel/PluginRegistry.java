package com.aurora.kernel;

import android.util.Log;

import com.aurora.externalservice.PluginEnvironment;
import com.aurora.plugin.BasicPlugin;
import com.aurora.plugin.Plugin;

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
     * A reference to the processing communicator
     */
    private ProcessingCommunicator mProcessingCommunicator;


    PluginRegistry(ProcessingCommunicator processingCommunicator, String configFileRef) {
        this.mProcessingCommunicator = processingCommunicator;

        // Load plugins
        constructPluginMap(configFileRef);
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
        // TODO: write back config file immediately
        mPluginsMap.remove(pluginName);
    }

    /**
     * Removes all plugins from the registry
     */
    void removeAllPlugins() {
        // TODO: write back config file immediately
        mPluginsMap.clear();
    }

    /**
     * Private helper method that reads the available plugins from a config file
     *
     * @param configFileRef a reference to where the config file can be found
     */
    private void constructPluginMap(String configFileRef) {
        // Create map
        mPluginsMap = new HashMap<>();

        Log.d("PluginRegistry", "This method should look for a file called "
                + configFileRef + ", to load the plugins");
    }
}
