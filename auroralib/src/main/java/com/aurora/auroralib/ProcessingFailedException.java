package com.aurora.auroralib;

/**
 * Exception that is thrown when the plugin's processing fails.
 */
public class ProcessingFailedException extends Exception {
    public ProcessingFailedException(String message) {
        super(message);
    }
}
