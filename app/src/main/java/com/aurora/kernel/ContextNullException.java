package com.aurora.kernel;

/**
 * Exception that should be thrown when someone tries to instantiate the Kernel, without a valid android context
 */
public class ContextNullException extends Exception {

    public ContextNullException(final String message) {
        super(message);
    }

}
