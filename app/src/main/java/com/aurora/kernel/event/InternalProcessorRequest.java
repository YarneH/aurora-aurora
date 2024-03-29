package com.aurora.kernel.event;

import com.aurora.plugin.InternalServices;

import java.io.InputStream;
import java.util.List;

/**
 * Event to request that a file is processed with a InternalProcessor
 *
 * @see InternalProcessorResponse
 */
public class InternalProcessorRequest implements Event {

    /**
     * The uri of the file to be opened
     */
    private String mFileUri;

    /**
     * The name of the file to be opened
     */
    private String mFileRef;

    /**
     * The type of the file (e.g. docx, pdf, txt)
     */
    private String mFileType;

    /**
     * An inputstream for the file
     */
    private InputStream mFile;

    /**
     * The internal services that should be run on the file
     */
    private List<InternalServices> mInternalServices;

    /**
     * Creates a new InternalProcessorRequest
     *
     * @param fileUri          the uri of the file to be opened
     * @param fileRef          the name of the file that should be processed internally
     * @param fileType         the file type of the file that should be processed
     * @param file             an inputstream to process the file
     * @param internalServices the set of internal services that should be run on the file
     */
    public InternalProcessorRequest(String fileUri, String fileRef, String fileType, InputStream file,
                                    List<InternalServices> internalServices) {
        mFileUri = fileUri;
        mFile = file;
        mFileType = fileType;
        mFileRef = fileRef;
        mInternalServices = internalServices;
    }

    /**
     * @return the uri of the file to be opened
     */
    public String getFileUri() {
        return mFileUri;
    }

    /**
     * @return the name of the file that will be processed internally
     */
    public String getFileRef() {
        return mFileRef;
    }

    /**
     * @return the file type of the file that will be processed
     */
    public String getFileType() {
        return mFileType;
    }

    /**
     * @return the inputstream to process the file
     */
    public InputStream getFile() {
        return mFile;
    }

    /**
     * @return The set of internal services that should be run on the file
     */
    public List<InternalServices> getInternalServices() {
        return mInternalServices;
    }
}
