package com.aurora.kernel.event;

import java.io.InputStream;

/**
 * Event to request that a file is processed with a InternalProcessor
 */
public class InternalProcessorRequest extends Event {

    private String mFileRef;
    private String mFileType;
    private InputStream mFile;

    //TODO: if needed add mechanism to add parameters to internal processor request

    public InternalProcessorRequest(String fileRef, String fileType, InputStream file) {
        this.mFile = file;
        this.mFileType = fileType;
        this.mFileRef = fileRef;
    }

    public String getFileRef() {
        return mFileRef;
    }

    public InputStream getFile() {
        return mFile;
    }

    public String getFileType() {
        return mFileType;
    }
}
