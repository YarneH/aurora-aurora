package com.aurora.kernel;

import android.util.Log;

import com.aurora.plugin.BasicPlugin;
import com.aurora.plugin.Plugin;
import com.aurora.plugin.PluginFragment;

import java.util.List;
import java.util.Map;

/**
 * Class that maintains link between a plugin name and plugin
 */
class PluginRegistry {
    /**
     * Maintains relation between a plugin name and a Plugin object
     */
    private Map<String, Plugin> mPluginsMap;

    private ProcessingCommunicator processingCommunicator;

    /**
     * private helper method to find a plugin based on the plugin name
     *
     * @param pluginName the name of the plugin
     * @return the plugin object associated with the name or null if not found
     */
    private Plugin resolvePlugin(String pluginName) {
        Log.d("PluginRegistry", "Not implemented yet!");
        return null;
    }

    /**
     * Finds the PLuginFragment to show given a plugin name
     *
     * @param pluginName the name of the plugin to load
     * @return the PluginFragment associated with the plugin name or null if not found
     */
    public PluginFragment loadPlugin(String pluginName) {
        Log.d("PluginRegistry", "Not implemented yet!");
        return null;
    }

    /**
     * Gets a list of all installed plugins
     *
     * @return List of Plugin objects with basic information
     */
    public List<BasicPlugin> getPlugins() {
        Log.d("PluginRegistry", "Not implemented yet!");
        return null;
    }
}
