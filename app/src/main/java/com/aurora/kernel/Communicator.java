package com.aurora.kernel;

/**
 * Common supertype of all communicators in the kernel
 */
public abstract class Communicator {
    /**
     * Keeps a reference to the communication bus used in the channel
     */
    protected Bus mBus;

    public Communicator(Bus mBus) {
        this.mBus = mBus;
    }
}
