package com.aurora.kernel;

import android.util.Log;

import com.aurora.internalservice.internalcache.InternalCache;
import com.aurora.internalservice.internalprocessor.InternalTextProcessing;
import com.aurora.plugin.Plugin;
import com.google.gson.Gson;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

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
    private static final String PLUGINS_CFG = "plugin-config.json";

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
        InternalTextProcessing internalTextProcessing = new InternalTextProcessing();
        this.mPluginInternalServiceCommunicator = new PluginInternalServiceCommunicator(mBus, internalTextProcessing);

        // Create cache
        InternalCache internalCache = new InternalCache();
        this.mAuroraInternalServiceCommunicator = new AuroraInternalServiceCommunicator(mBus, internalCache);

        // Initialize plugin config
        initializePluginConfig();
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

    /**
     * Private helper method that checks if the plugin-config file already exists, and creates one when necessary
     */
    private void initializePluginConfig() {
        File file = new File(PLUGINS_CFG);

        // If the file does not exist, create one and write an empty JSON array to it
        if (!file.exists()) {
            try (Writer writer = new BufferedWriter(new FileWriter(file))) {
                Gson gson = new Gson();
                String jsonPlugin = gson.toJson(new Plugin[]{}, Plugin[].class);

                writer.write(jsonPlugin);
                writer.flush();

            } catch (IOException e) {
                Log.e("Kernel", "Something went wrong trying to create the file " + PLUGINS_CFG + ".");
            }
        }
    }
}
