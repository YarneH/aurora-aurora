package com.aurora.kernel;

/**
 * Communicator that communicates with Plugin processors
 */
public class ProcessingCommunicator extends Communicator {

    /**
     * Creates a ProcessingCommunicator. There should be only one instance at a time
     * @param mBus a reference to the unique bus instance that all communicators should use to communicate events
     */
    public ProcessingCommunicator(Bus mBus) {
        super(mBus);

    }
}
