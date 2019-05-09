package com.aurora.auroralib;

import android.content.Context;
import android.content.Intent;

import com.aurora.auroralib.cache.CacheServiceCaller;
import com.aurora.auroralib.cache.ProcessorCacheThread;

/**
 * Superclass for a 'Communicator', i.e. the interface between plugin environment and plugin processor
 */
public abstract class ProcessorCommunicator {
    /**
     * The unique name of the plugin (for example com.aurora.basicplugin)
     */
    protected String mUniquePluginName;

    /**
     * A CacheServiceCaller for caching the processed file
     */
    protected CacheServiceCaller mCacheServiceCaller;

    /**
     * The android context
     */
    protected Context mContext;

    public ProcessorCommunicator(String uniquePluginName, Context context) {
        mUniquePluginName = uniquePluginName;
        mContext = context;
        mCacheServiceCaller = new CacheServiceCaller(context);
    }

    /**
     * Processes an ExtractedText object (received from Aurora) and returns a PluginObject (or an object
     * of a subclass specific for the current plugin)
     *
     * @param extractedText    The text that was extracted after Aurora's internal processing
     * @return The PluginObject that is the result of the plugin's processing of the extractedText
     */
    protected abstract PluginObject process(ExtractedText extractedText) throws ProcessingFailedException;



    /**
     * Executes the pipeline that consists of processing the ExtractedText to receive a PluginObject
     * and then caching this
     *
     * @param extractedText the text extracted by aurora
     * @return the resulting object or null if something went wrong
     */
    public final PluginObject pipeline(ExtractedText extractedText) {
        try {
            PluginObject pluginObject = process(extractedText);
            ProcessorCacheThread processorCacheThread = new ProcessorCacheThread(pluginObject, mCacheServiceCaller);
            processorCacheThread.start();
            return pluginObject;
        } catch (ProcessingFailedException e) {
            // If processing failed, start intent to open activity in aurora
            Intent intent = new Intent(Constants.PLUGIN_PROCESSING_FAILED_ACTION);
            intent.putExtra(Constants.PLUGIN_PROCESSING_FAILED_REASON, e.getMessage());

            mContext.startActivity(intent);
        }

        // If not yet returned, return null
        return null;
    }

}

