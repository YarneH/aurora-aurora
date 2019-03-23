package com.aurora.kernel;

import com.aurora.internalservice.internalcache.InternalCache;
import com.aurora.internalservice.internalprocessor.InternalTextProcessor;

/**
 * Wrapper class that wraps all communicators and instantiates the unique event bus
 */
public final class Kernel {
    private Bus mBus;

    private AuroraCommunicator mAuroraCommunicator;
    private PluginCommunicator mPluginCommunicator;
    private ProcessingCommunicator mProcessingCommunicator;
    private PluginInternalServiceCommunicator mPluginInternalServiceCommunicator;
    private AuroraInternalServiceCommunicator mAuroraInternalServiceCommunicator;

    private PluginRegistry mPluginRegistry;

    // TODO: change this if necessary
    private static final String PLUGINS_CFG = "plugins.cfg";

    /**
     * Starts and creates all communicators, keeping references
     */
    public Kernel() {
        this.mBus = new Bus();

        this.mAuroraCommunicator = new AuroraCommunicator(mBus);

        this.mProcessingCommunicator = new ProcessingCommunicator(mBus);
        this.mPluginRegistry = new PluginRegistry(mProcessingCommunicator, PLUGINS_CFG);
        this.mPluginCommunicator = new PluginCommunicator(mBus, mPluginRegistry);

        // Create internal text processor for the PluginInternalServiceCommunicator
        InternalTextProcessor internalTextProcessing = new InternalTextProcessor();
        this.mPluginInternalServiceCommunicator = new PluginInternalServiceCommunicator(mBus, internalTextProcessing);

        // Create cache
        InternalCache internalCache = new InternalCache();
        this.mAuroraInternalServiceCommunicator = new AuroraInternalServiceCommunicator(mBus, internalCache);
    }


    /**
     * gets a reference to the aurora communicator
     *
     * @return AuroraCommunicator
     */
    public AuroraCommunicator getAuroraCommunicator() {
        return mAuroraCommunicator;
    }

    public PluginCommunicator getPluginCommunicator() {
        return mPluginCommunicator;
    }

    public ProcessingCommunicator getProcessingCommunicator() {
        return mProcessingCommunicator;
    }

    public PluginInternalServiceCommunicator getPluginInternalServiceCommunicator() {
        return mPluginInternalServiceCommunicator;
    }

    public AuroraInternalServiceCommunicator getAuroraInternalServiceCommunicator() {
        return mAuroraInternalServiceCommunicator;
    }

}
