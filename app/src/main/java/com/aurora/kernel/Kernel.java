package com.aurora.kernel;

public class Kernel {
    private AuroraCommunicator mAuroraCommunicator;

    /**
     * Starts and creates all communicator, keeping references
     */
    private Kernel() {
        this.mAuroraCommunicator = new AuroraCommunicator();
    }


    /**
     * gets a reference to the aurora communicator
     *
     * @return AuroraCommunicator
     */
    public AuroraCommunicator getAuroraCommunicator() {
        return mAuroraCommunicator;
    }
}
