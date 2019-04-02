package com.aurora.kernel.event;

import java.io.InputStream;

/**
 * Event to request that a file is processed with a InternalProcessor
 */
public class InternalProcessorRequest extends Event {

    private InputStream mFile;
    private String mFileRef;
    private String mType;
    //TODO: if needed add mechanism to add parameters to internal processor request

    public InternalProcessorRequest(InputStream file, String fileRef, String type) {
        this.mFile = file;
        this.mFileRef = fileRef;
        this.mType = type;
    }

    public String getFileRef() {
        return mFileRef;
    }

    public InputStream getFile() {
        return mFile;
    }

    public String getType() {
        return mType;
    }
}
