package com.aurora.kernel;

/**
 * Event to request that a file is opened with a plugin
 */
class PluginEvent extends Event {
    private String mPluginName;
    private String mFileRef;
}
