package com.aurora.kernel;

/**
 * Exception that should be thrown when someone tries to instantiate the Kernel, without a valid android context
 */
public class ContextNullException extends Exception {
    public ContextNullException() {
    }

    public ContextNullException(String message) {
        super(message);
    }

    public ContextNullException(String message, Throwable cause) {
        super(message, cause);
    }

    public ContextNullException(Throwable cause) {
        super(cause);
    }
}
