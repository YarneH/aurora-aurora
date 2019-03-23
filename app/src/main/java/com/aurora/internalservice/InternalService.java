package com.aurora.internalservice;

import com.aurora.internalservice.internalprocessor.FileTypeNotSupportedException;

import java.io.InputStream;

/**
 * Abstract class maintaining some properties and methods for an InternalService
 */
public interface InternalService {

    /**
     * processes a file internally
     *
     * @param fileRef a reference to where the file can be found
     * @return The internally processed file
     */
    InternallyProcessedFile processFile(InputStream file, String fileRef) throws FileTypeNotSupportedException;
}
