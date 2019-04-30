package com.aurora.kernel;

import android.content.Context;
import android.util.Log;

import com.android.volley.toolbox.Volley;
import com.aurora.internalservice.internalcache.InternalCache;
import com.aurora.internalservice.internalprocessor.InternalTextProcessor;
import com.aurora.plugin.Plugin;
import com.google.gson.Gson;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

import io.reactivex.schedulers.Schedulers;

/**
 * Wrapper class that wraps all communicators and instantiates the unique event bus.
 * This class will perform the initial 'configuration' or 'bootstrapping' of everything kernel related.
 */
public final class Kernel {
    /**
     * A constant indicating how the plugin config file is named
     */
    private static final String PLUGINS_CFG = "plugin-config.json";
    /**
     * A reference to the unique bus instance that should be used among all communicators
     */
    private Bus mBus;
    /**
     * A reference to the android context
     */
    private Context mContext;
    /**
     * A reference to the AuroraCommunicator
     */
    private AuroraCommunicator mAuroraCommunicator;
    /**
     * A reference to the PluginCommunicator
     */
    private PluginCommunicator mPluginCommunicator;
    /**
     * A reference to the ProcessingCommunicator
     */
    private ProcessingCommunicator mProcessingCommunicator;
    /**
     * A reference to the PluginInternalServiceCommunicator
     */
    private PluginInternalServiceCommunicator mPluginInternalServiceCommunicator;
    /**
     * A reference to the AuroraInternalServiceCommunicator
     */
    private AuroraInternalServiceCommunicator mAuroraInternalServiceCommunicator;

    // TODO: change this if necessary
    /**
     * A reference to the plugin registry
     */
    private PluginRegistry mPluginRegistry;

    /**
     * Starts and creates all communicators, keeping references
     *
     * @param applicationContext the android application context. Make sure this is the application context and not
     *                           the context related to the activity (get with this.getApplicationContext) to avoid
     *                           memory leaks.
     */
    public Kernel(Context applicationContext) {
        this.mContext = applicationContext;

        // Create 1 bus to be shared among all communicators
        this.mBus = new Bus(Schedulers.computation());

        // Initialize plugin config
        initializePluginConfig();

        // Create plugin registry that keeps info of the plugins
        this.mPluginRegistry = new PluginRegistry(mProcessingCommunicator, PLUGINS_CFG, applicationContext);

        // Create the different communicators
        this.mAuroraCommunicator = new AuroraCommunicator(mBus, mPluginRegistry);
        this.mProcessingCommunicator = new ProcessingCommunicator(mBus);
        this.mPluginCommunicator = new PluginCommunicator(mBus, mPluginRegistry);

        // Create internal text processor for the PluginInternalServiceCommunicator
        InternalTextProcessor internalTextProcessing = new InternalTextProcessor();
        this.mPluginInternalServiceCommunicator = new PluginInternalServiceCommunicator(mBus,
                internalTextProcessing, Volley.newRequestQueue(applicationContext));

        // Create cache
        InternalCache internalCache = new InternalCache(applicationContext);
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

    /**
     * @return the PluginCommunicator
     */
    public PluginCommunicator getPluginCommunicator() {
        return mPluginCommunicator;
    }

    /**
     * @return the ProcessingCommunicator
     */
    public ProcessingCommunicator getProcessingCommunicator() {
        return mProcessingCommunicator;
    }

    /**
     * @return the PluginInternalServiceCommunicator
     */
    public PluginInternalServiceCommunicator getPluginInternalServiceCommunicator() {
        return mPluginInternalServiceCommunicator;
    }

    /**
     * @return the AuroraInternalServiceCommunicator
     */
    public AuroraInternalServiceCommunicator getAuroraInternalServiceCommunicator() {
        return mAuroraInternalServiceCommunicator;
    }

    /**
     * Private helper method that checks if the plugin-config file already exists, and creates one when necessary
     */
    private void initializePluginConfig() {
        File file = new File(mContext.getFilesDir(), PLUGINS_CFG);

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
