package com.aurora.kernel;

import android.util.Log;

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
     * TODO Define test to check if all objects are unique and not null
     */
    private Kernel() {
        this.mBus = new Bus();

        this.mAuroraCommunicator = new AuroraCommunicator(mBus);

        this.mProcessingCommunicator = new ProcessingCommunicator(mBus);
        this.mPluginRegistry = new PluginRegistry(mProcessingCommunicator, PLUGINS_CFG);
        this.mPluginCommunicator = new PluginCommunicator(mBus, mPluginRegistry);

        this.mPluginInternalServiceCommunicator = new PluginInternalServiceCommunicator(mBus);
        this.mAuroraInternalServiceCommunicator = new AuroraInternalServiceCommunicator(mBus);

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

    public Bus getBus() {
        return mBus;
    }

    /**
     * Private helper method that checks if the plugin-config file already exists, and creates one when necessary
     */
    private void initializePluginConfig() {
        File file = new File(PLUGINS_CFG);

        // If the file does not exist, create one and write an empty JSON array to it
        if (!file.exists()) {
            try {
                Gson gson = new Gson();
                String jsonPlugin = gson.toJson(new Plugin[]{}, Plugin[].class);

                Writer writer = new BufferedWriter(new FileWriter(file));
                writer.write(jsonPlugin);
                writer.flush();
                writer.close();
            } catch (IOException e) {
                Log.e("Kernel", "Something went wrong trying to create the file " + PLUGINS_CFG + ".");
            }
        }
    }
}
