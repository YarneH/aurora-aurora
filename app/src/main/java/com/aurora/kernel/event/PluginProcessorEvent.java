package com.aurora.kernel.event;

import com.aurora.plugin.PluginProcessor;

/**
 * Event to request that a file is processed with a PluginProcessor
 */
public class PluginProcessorEvent extends Event {
    private PluginProcessor mPluginProcessor;
    private String mFileRef;

    public PluginProcessorEvent(PluginProcessor pluginProcessor, String fileRef) {
        this.mPluginProcessor = pluginProcessor;
        this.mFileRef = fileRef;
    }

    public String getFileRef() {
        return mFileRef;
    }

    public PluginProcessor getPluginProcessor() {
        return mPluginProcessor;
    }
}
