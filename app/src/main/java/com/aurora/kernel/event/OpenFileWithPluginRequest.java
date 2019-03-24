package com.aurora.kernel.event;

import java.io.InputStream;

/**
 * Event to request that a file is opened with a plugin
 */
public class OpenFileWithPluginRequest extends Event {
    private String mPluginName;
    private InputStream mFile;
    private String mFileRef;

    public OpenFileWithPluginRequest(String pluginName, InputStream file, String fileRef){
        super();
        this.mPluginName = pluginName;
        this.mFile = file;
        this.mFileRef = fileRef;
    }

    public String getPluginName() {
        return mPluginName;
    }

    public InputStream getFile() {
        return mFile;
    }

    public String getFileRef() {
        return mFileRef;
    }
}
