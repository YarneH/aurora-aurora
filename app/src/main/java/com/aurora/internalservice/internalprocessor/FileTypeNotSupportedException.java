package com.aurora.internalservice.internalprocessor;

/**
 * Error if the file type being opened is not supported by Aurora
 */
public class FileTypeNotSupportedException extends Exception {
    public FileTypeNotSupportedException(String message) {
        super(message);
    }
}
