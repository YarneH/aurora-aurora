package com.aurora.kernel;

public final class Kernel {
    private Bus mBus;

    private AuroraCommunicator mAuroraCommunicator;
    private PluginCommunicator mPluginCommunicator;
    private ProcessingCommunicator mProcessingCommunicator;
    private PluginInternalServiceCommunicator mPluginInternalServiceCommunicator;
    private AuroraInternalServiceCommunicator mAuroraInternalServiceCommunicator;

    private PluginRegistry mPluginRegistry;

    // TODO: change this if necessary
    private static final String pluginConfigFileRef = "plugins.cfg";

    /**
     * Starts and creates all communicators, keeping references
     * TODO Define test to check if all objects are unique and not null
     */
    private Kernel() {
        this.mBus = new Bus();

        this.mAuroraCommunicator = new AuroraCommunicator(mBus);

        this.mProcessingCommunicator = new ProcessingCommunicator(mBus);
        this.mPluginRegistry = new PluginRegistry(mProcessingCommunicator, pluginConfigFileRef);
        this.mPluginCommunicator = new PluginCommunicator(mBus, mPluginRegistry);

        this.mPluginInternalServiceCommunicator = new PluginInternalServiceCommunicator(mBus);
        this.mAuroraInternalServiceCommunicator = new AuroraInternalServiceCommunicator(mBus);
    }


    /**
     * gets a reference to the aurora communicator
     *
     * @return AuroraCommunicator
     */
    public AuroraCommunicator getAuroraCommunicator() {
        return mAuroraCommunicator;
    }

    public Bus getBus() {
        return mBus;
    }


}
