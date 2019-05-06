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

    /**
     * Creates a new CachedProcessedFile instance
     *
     * @param jsonRepresentation the json representation of the processed file
     * @param fileRef            the file reference to the original file
     * @param uniquePluginName   the name of the plugin that the file was processed with
     */
    public CachedProcessedFile(String jsonRepresentation, String fileRef, String uniquePluginName) {
        mJsonRepresentation = jsonRepresentation;
        mFileRef = fileRef;
        mUniquePluginName = uniquePluginName;
    }

    /**
     * @return the json representation of the processed file, in String format
     */
    public String getJsonRepresentation() {
        return mJsonRepresentation;
    }

    /**
     * @return the file reference to the original file
     */
    public String getFileRef() {
        return mFileRef;
    }

    /**
     * @return the name of the plugin that the file was processed with
     */
    public String getUniquePluginName() {
        return mUniquePluginName;
    }
}
