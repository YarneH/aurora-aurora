package com.aurora.kernel.event;

/**
 * Event to request that a file is processed with a InternalProcessor
 */
public class InternalProcessorRequest extends Event {

    private String mFileRef;
    // TODO: if needed add mechanism to add parameters to internal processor request

    public InternalProcessorRequest(String fileRef) {
        this.mFileRef = fileRef;
    }

    public String getFileRef() {
        return mFileRef;
    }
}
