package com.aurora.kernel.event;

import com.aurora.plugin.InternalServices;

import java.io.InputStream;
import java.util.Set;

/**
 * Event to request that a file is processed with a InternalProcessor
 */
public class InternalProcessorRequest extends Event {

    /**
     * A reference to the file to be opened
     */
    private String mFileRef;

    /**
     * The type of the file (e.g. docx, pdf, txt)
     */
    private String mFileType;

    /**
     * An inputstream
     */
    private InputStream mFile;

    /**
     * The internal services that should be run on the file
     */
    private Set<InternalServices> mInternalServices;

    //TODO: if needed add mechanism to add parameters to internal processor request

    public InternalProcessorRequest(String fileRef, String fileType, InputStream file,
                                    Set<InternalServices> internalServices) {
        mFile = file;
        mFileType = fileType;
        mFileRef = fileRef;
        mInternalServices = internalServices;
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

    public Set<InternalServices> getInternalServices() {
        return mInternalServices;
    }
}
