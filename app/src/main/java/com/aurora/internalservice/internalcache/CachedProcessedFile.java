package com.aurora.internalservice.internalcache;

import com.aurora.auroralib.InternallyProcessedFile;

/**
 * This class will contain the processed file which is cached
 */
public class CachedProcessedFile implements InternallyProcessedFile {
    /**
     * The json representation of the object to represent
     */
    private String mJsonRepresentation;

    /**
     * A reference to the file in case the plugin cannot open the json representation
     * or the json representation is empty.
     */
    private String mFileRef;

    public CachedProcessedFile(String jsonRepresentation, String fileRef) {
        mJsonRepresentation = jsonRepresentation;
        mFileRef = fileRef;
    }

    public String getJsonRepresentation() {
        return mJsonRepresentation;
    }

    public String getFileRef() {
        return mFileRef;
    }
}
