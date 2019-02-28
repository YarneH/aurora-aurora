package com.aurora.internalservice;

/**
 * Abstract class maintaining some properties and methods for an
 */
public abstract class InternalService {

    // TODO: Will probably return something instead of void!
    /**
     * processes a file internally
     *
     * @param fileRef a reference to where the file can be found
     */
    public abstract void process(String fileRef);
}
