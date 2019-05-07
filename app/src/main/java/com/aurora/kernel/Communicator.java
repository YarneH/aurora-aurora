package com.aurora.kernel;

import android.support.annotation.NonNull;

/**
 * Common supertype of all communicators in the kernel
 */
public abstract class Communicator {
    /**
     * Keeps a reference to the communication bus used in the channel
     * Final so it cannot be changed afterwards
     */
    protected final Bus mBus;

    /**
     * Creates a new communicator instance
     *
     * @param mBus a reference to the unique bus instance that should be used by communicators to communicate events
     */
    protected Communicator(@NonNull final Bus mBus) {
        this.mBus = mBus;
    }
}
