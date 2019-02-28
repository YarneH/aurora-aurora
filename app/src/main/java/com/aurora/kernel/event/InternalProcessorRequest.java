package com.aurora.kernel.event;

import java.util.Map;

/**
 * Event to request that a file is processed with a InternalProcessor
 */
public class InternalProcessorRequest extends Event {

    private String mFileRef;
    private Map<String, ?> mParameters;

    public InternalProcessorRequest(String fileRef, Map<String, ?> parameters) {
        this.mFileRef = fileRef;
        this.mParameters = parameters;
    }

    public String getFileRef() {
        return mFileRef;
    }

    public Map<String, ?> getParameters() {
        return mParameters;
    }
}
