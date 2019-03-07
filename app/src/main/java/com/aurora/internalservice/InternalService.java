package com.aurora.internalservice;

import com.aurora.internalservice.internalprocessor.FileTypeNotSupportedException;

/**
 * Abstract class maintaining some properties and methods for an InternalService
 */
public interface InternalService {

    // TODO: Will probably return something instead of void!
    /**
     * processes a file internally
     *
     * @param fileRef a reference to where the file can be found
     * @return The internally processed file
     */
    InternallyProcessedFile processFile(String fileRef) throws FileTypeNotSupportedException;
}
