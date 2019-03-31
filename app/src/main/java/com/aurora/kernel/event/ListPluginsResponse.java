package com.aurora.kernel.event;

import com.aurora.plugin.Plugin;

import java.util.List;

/**
 * Event containing a list of available plugins
 */
public class ListPluginsResponse extends Event {
    private List<Plugin> plugins;

    public ListPluginsResponse(List<Plugin> plugins) {
        this.plugins = plugins;
    }

    public List<Plugin> getPlugins() {
        return plugins;
    }
}
