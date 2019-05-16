package com.aurora.auroralib;

import android.content.Context;
import android.support.annotation.NonNull;
import android.content.Intent;

import com.aurora.auroralib.cache.CacheServiceCaller;
import com.aurora.auroralib.cache.ProcessorCacheThread;

/**
 * Superclass for a 'Communicator', i.e. the interface between plugin environment and plugin
 * processor. A processor should implement {@link #process(ExtractedText)}. This method will
 * automatically be executed when {@link #pipeline(ExtractedText)} is called. This will ensure
 * that caching in Aurora is performed.
 */
@SuppressWarnings("unused")
public abstract class ProcessorCommunicator {
    /**
     * The unique name of the plugin (for example com.aurora.basicplugin)
     */
    protected String mUniquePluginName;

    /**
     * A CacheServiceCaller for caching the processed file
     */
    @SuppressWarnings("WeakerAccess")
    protected CacheServiceCaller mCacheServiceCaller;

    /**
     * The android context
     */
    protected Context mContext;

    /**
     * Creates a new instance of a ProcessorCommunicator.
     * Mind that this is an abstract class so no actual instances can be created. This is just to make sure that
     * Communicators in the plugin have these arguments and that a {@link CacheServiceCaller} is
     * instantiated
     *
     * @param context an android context
     */
    @SuppressWarnings("unused")
    public ProcessorCommunicator(@NonNull final Context context) {
        mUniquePluginName = context.getPackageName();
        mContext = context;
        mCacheServiceCaller = new CacheServiceCaller(context);
    }

    /**
     * Processes an ExtractedText object (received from Aurora) and returns a PluginObject (or an
     * object of a subclass specific for the current plugin)
     *
     * @param extractedText The text that was extracted after Aurora's internal processing
     * @return The PluginObject that is the result of the plugin's processing of the extractedText
     * @throws ProcessingFailedException when the processing fails for whatever reason, this exception should be thrown
     */
    @SuppressWarnings("WeakerAccess")
    protected abstract PluginObject process(@NonNull final ExtractedText extractedText)
            throws ProcessingFailedException;


    /**
     * Executes the pipeline that consists of processing the ExtractedText to receive a PluginObject
     * and then caches the object. In case the processing fails, this will return to Aurora.
     *
     * @param extractedText the text extracted by aurora
     * @return the PluginObject that is returned by {@link #process(ExtractedText)} or null if something
     * went wrong.
     */
    @SuppressWarnings({"unused", "WeakerAccess"})
    public final PluginObject pipeline(@NonNull final ExtractedText extractedText) {
        try {
            PluginObject pluginObject = process(extractedText);
            ProcessorCacheThread processorCacheThread = new ProcessorCacheThread(pluginObject,
                    mCacheServiceCaller);
            processorCacheThread.start();
            return pluginObject;
        } catch (ProcessingFailedException e) {
            // If processing failed, start intent to open activity in aurora
            Intent intent = new Intent(Constants.PLUGIN_PROCESSING_FAILED_ACTION);
            intent.putExtra(Constants.PLUGIN_PROCESSING_FAILED_REASON, e.getMessage());
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            mContext.startActivity(intent);
        }

        // If not yet returned, return null
        return null;
    }

    /**
     * Method to return to the aurora main screen.
     * @param context the android context
     */
    public static final void returnToAurora(Context context) {
        Intent intent = context.getPackageManager().getLaunchIntentForPackage(Constants.AURORA);
        if (intent != null) {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
    }

}

