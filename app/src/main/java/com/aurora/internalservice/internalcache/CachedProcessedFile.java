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

    /**
     *
     */
    private String mUniquePluginName;

    public CachedProcessedFile(String jsonRepresentation, String fileRef, String uniquePluginName) {
        mJsonRepresentation = jsonRepresentation;
        mFileRef = fileRef;
        mUniquePluginName = uniquePluginName;
    }

    public String getJsonRepresentation() {
        return mJsonRepresentation;
    }

    public String getFileRef() {
        return mFileRef;
    }

    public String getUniquePluginName() {
        return mUniquePluginName;
    }
}
