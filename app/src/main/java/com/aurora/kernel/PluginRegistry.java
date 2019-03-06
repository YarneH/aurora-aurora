package com.aurora.kernel;

import android.util.Log;

import com.aurora.externalservice.PluginEnvironment;
import com.aurora.plugin.BasicPlugin;
import com.aurora.plugin.Plugin;

import java.util.ArrayList;
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


    public PluginRegistry(ProcessingCommunicator processingCommunicator) {
        this.mProcessingCommunicator = processingCommunicator;

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
    public PluginEnvironment loadPlugin(String pluginName) {
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
    public List<BasicPlugin> getPlugins() {
        Log.d("PluginRegistry", "Not implemented yet!");
        return new ArrayList<>();
    }


    /**
     * Private helper method to read the plugins from a config file and load them into a map
     */
    private void constructPluginMap() {
        Log.d("PluginRegistry", "Not implemented yet!");
    }
}
