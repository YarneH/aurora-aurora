package com.aurora.kernel;

/**
 * Common supertype of all communicators in the kernel
 */
public abstract class Communicator {
    /**
     * Keeps a reference to the communication bus used in the channel
     */
    protected Bus mBus;

    /**
     * Creates a new communicator instance
     *
     * @param mBus a reference to the unique bus instance that should be used by communicators to communicate events
     */
    protected Communicator(Bus mBus) {
        this.mBus = mBus;
    }
}
