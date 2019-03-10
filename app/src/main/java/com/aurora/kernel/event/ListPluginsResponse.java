package com.aurora.kernel.event;

import com.aurora.plugin.BasicPlugin;

import java.util.List;

/**
 * Event containing a list of available plugins
 */
public class ListPluginsResponse extends Event {
    private List<BasicPlugin> plugins;

    public ListPluginsResponse(List<BasicPlugin> plugins) {
        this.plugins = plugins;
    }

    public List<BasicPlugin> getPlugins() {
        return plugins;
    }
}
