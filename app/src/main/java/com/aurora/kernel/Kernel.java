package com.aurora.kernel;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.android.volley.toolbox.Volley;
import com.aurora.internalservice.internalcache.InternalCache;
import com.aurora.internalservice.internalprocessor.InternalTextProcessor;
import com.aurora.internalservice.internaltranslation.Translator;
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
 * This class is a singleton class to allow it to be accessible from different places, without the risk of having
 * multiple separate instances. For example, the kernel may be needed both in the activities of aurora, and in the
 * services that aurora provides to the plugins.
 */
public final class Kernel {

    /**
     * A constant indicating how the plugin config file is named
     */
    private static final String PLUGINS_CFG = "plugin-config.json";

    /**
     * A static reference to the Kernel if it has been created
     */
    private static Kernel sKernel = null;

    /**
     * A reference to the AuroraCommunicator
     */
    private static AuroraCommunicator sAuroraCommunicator;
    /**
     * A reference to the PluginCommunicator
     */
    private static PluginCommunicator sPluginCommunicator;
    /**
     * A reference to the ProcessingCommunicator
     */
    private static ProcessingCommunicator sProcessingCommunicator;
    /**
     * A reference to the PluginInternalServiceCommunicator
     */
    private static PluginInternalServiceCommunicator sPluginInternalServiceCommunicator;
    /**
     * A reference to the AuroraInternalServiceCommunicator
     */
    private static AuroraInternalServiceCommunicator sAuroraInternalServiceCommunicator;

    /**
     * private constructor so no new instance can be constructed
     */
    private Kernel() {
    }

    /**
     * Returns the singleton kernel if it has already been initialized.
     * In case the kernel was not yet initialized, this method will throw an exception
     *
     * @return the singleton kernel instance, if it was initialized before
     * @throws IllegalArgumentException when the kernel has not yet been initialized and the applicationContext is null
     */
    public static @NonNull Kernel getInstance() throws ContextNullException {
        return getInstance(null);
    }

    /**
     * Returns the singleton kernel instance after it has been created.
     * If no instance exists yet, this method will take care of it, but the applicationContext may not be null in that
     * case. If the instance already exists the applicationContext argument will not be used
     *
     * @param applicationContext the android application context
     * @return the singleton kernel instance
     * @throws IllegalArgumentException when the kernel has not yet been initialized and the applicationContext is null
     */
    public static @NonNull Kernel getInstance(Context applicationContext) throws ContextNullException {
        if (sKernel == null && applicationContext != null) {
            // Initialize kernel
            return initialize(applicationContext);
        } else if (sKernel == null) {
            // Kernel not initialzed but application context cannot be used to initialized
            throw new ContextNullException("The kernel can not be initialized with " +
                    "applicationContext equal to null!");
        } else {
            // Already initialized, return existing instance
            return sKernel;
        }
    }

    /**
     * Private method that creates the singleton kernel instance with all the communicators.
     *
     * @param applicationContext the android application context needed for some communicators
     * @return the singleton kernel instance
     */
    private static @NonNull Kernel initialize(@NonNull final Context applicationContext) {
        sKernel = new Kernel();

        // Create 1 bus to be shared among all communicators
        Bus bus = new Bus(Schedulers.computation());

        // Initialize plugin config
        initializePluginConfig(applicationContext);

        // Create the different communicators
        sAuroraCommunicator = new AuroraCommunicator(bus, applicationContext);
        sProcessingCommunicator = new ProcessingCommunicator(bus);
        sPluginCommunicator = new PluginCommunicator(bus);

        // Create internal text processor for the PluginInternalServiceCommunicator
        InternalTextProcessor internalTextProcessing = new InternalTextProcessor();

        sPluginInternalServiceCommunicator = new PluginInternalServiceCommunicator(bus,
                internalTextProcessing, new Translator(Volley.newRequestQueue(applicationContext)));


        // Create cache
        InternalCache internalCache = new InternalCache(applicationContext);
        sAuroraInternalServiceCommunicator = new AuroraInternalServiceCommunicator(bus, internalCache);
        return sKernel;
    }

    /**
     * Private helper method that checks if the plugin-config file already exists, and creates one when necessary
     *
     * @param applicationContext the android application context, needed for writing files
     */
    private static void initializePluginConfig(@NonNull final Context applicationContext) {
        File file = new File(applicationContext.getFilesDir(), PLUGINS_CFG);

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

    /**
     * gets a reference to the aurora communicator
     *
     * @return AuroraCommunicator
     */
    public @NonNull AuroraCommunicator getAuroraCommunicator() {
        return sAuroraCommunicator;
    }

    /**
     * @return the PluginCommunicator
     */
    public @NonNull PluginCommunicator getPluginCommunicator() {
        return sPluginCommunicator;
    }

    /**
     * @return the ProcessingCommunicator
     */
    public @NonNull ProcessingCommunicator getProcessingCommunicator() {
        return sProcessingCommunicator;
    }

    /**
     * @return the PluginInternalServiceCommunicator
     */
    public @NonNull PluginInternalServiceCommunicator getPluginInternalServiceCommunicator() {
        return sPluginInternalServiceCommunicator;
    }

    /**
     * @return the AuroraInternalServiceCommunicator
     */
    public @NonNull AuroraInternalServiceCommunicator getAuroraInternalServiceCommunicator() {
        return sAuroraInternalServiceCommunicator;
    }
}
