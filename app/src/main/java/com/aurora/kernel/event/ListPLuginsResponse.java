package com.aurora.kernel.event;

import com.aurora.plugin.BasicPlugin;

import java.util.List;

/**
 * Event containing a list of available plugins
 */
public class ListPLuginsResponse extends Event {
    List<BasicPlugin> plugins;

    public ListPLuginsResponse(List<BasicPlugin> plugins) {
        this.plugins = plugins;
    }

    public List<BasicPlugin> getPlugins() {
        return plugins;
    }
}
