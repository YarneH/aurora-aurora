package com.aurora.internalservice.internalprocessor;

/**
 * This error is thrown when the document being opened contains not supported content
 * For example a PDF which is not tagged
 */
public class DocumentNotSupportedException extends Exception {
    public DocumentNotSupportedException(String message) {
        super(message);
    }
}
