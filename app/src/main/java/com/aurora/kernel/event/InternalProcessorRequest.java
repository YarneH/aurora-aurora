package com.aurora.kernel.event;

import java.io.InputStream;

/**
 * Event to request that a file is processed with a InternalProcessor
 */
public class InternalProcessorRequest extends Event {

    private InputStream mFile;
    private String mFileRef;

    /**
     * File type, which will be automatically inferred from the file ref
     */
    private String mType;
    //TODO: if needed add mechanism to add parameters to internal processor request

    public InternalProcessorRequest(InputStream file, String fileRef) {
        this.mFile = file;
        this.mFileRef = fileRef;

        int lastDotIndex = fileRef.lastIndexOf('.');
        if (lastDotIndex > fileRef.lastIndexOf('/') &&
                lastDotIndex < fileRef.length() - 1) {
            mType = fileRef.substring(fileRef.lastIndexOf('.') + 1);
        } else {
            mType = null;
        }
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

    // TODO remove this! this is just for testing purposes!
    public void setType(String type) {
        mType = type;
    }
}
