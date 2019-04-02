package com.aurora.internalservice.internalcache;

import com.aurora.auroralib.InternallyProcessedFile;

/**
 * This class will contain the processed file which is cached
 */
public class CachedProcessedFile implements InternallyProcessedFile {
    private String mJsonRepresentation;

    public CachedProcessedFile(String jsonRepresentation) {
        mJsonRepresentation = jsonRepresentation;
    }

    public String getJsonRepresentation() {
        return mJsonRepresentation;
    }
}
