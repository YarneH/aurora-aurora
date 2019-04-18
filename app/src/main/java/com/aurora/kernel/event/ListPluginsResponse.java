package com.aurora.kernel.event;

import com.aurora.plugin.Plugin;

import java.util.List;

/**
 * Event containing a list of available plugins
 * @see ListPluginsRequest
 */
public class ListPluginsResponse extends Event {
    /**
     * A list of installed plugins of the system, with their metadata
     */
    private List<Plugin> plugins;

    /**
     * Creates a new ListPluginsResponse
     *
     * @param plugins a list list of installed plugins with metadata
     */
    public ListPluginsResponse(List<Plugin> plugins) {
        this.plugins = plugins;
    }

    /**
     * @return a list of installed plugins with metadata
     */
    public List<Plugin> getPlugins() {
        return plugins;
    }
}
