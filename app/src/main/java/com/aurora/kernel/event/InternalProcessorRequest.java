package com.aurora.kernel.event;

import java.io.InputStream;

/**
 * Event to request that a file is processed with a InternalProcessor
 */
public class InternalProcessorRequest extends Event {

    private InputStream mFile;
    private String mFileRef;
    //TODO: if needed add mechanism to add parameters to internal processor request

    public InternalProcessorRequest(InputStream file, String fileRef) {
        this.mFileRef = fileRef;
    }

    public String getFileRef() {
        return mFileRef;
    }

    public InputStream getFile() {
        return mFile;
    }
}
