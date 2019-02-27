package com.aurora.kernel.event;

/**
 * Event to request that a file is opened with a plugin
 */
public class OpenFileWithPluginRequest extends Event {
    private String mPluginName;
    private String mFileRef;

    public OpenFileWithPluginRequest(String pluginName, String fileRef){
        super();
        this.mPluginName = pluginName;
        this.mFileRef = fileRef;
    }

    public String getPluginName() {
        return mPluginName;
    }

    public String getFileRef() {
        return mFileRef;
    }
}
