package com.aurora.auroralib;

import android.content.Context;

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
    protected CacheServiceCaller mCacheServiceCaller;

    /**
     * The android context
     */
    protected Context mContext;

    @SuppressWarnings("unused")
    public ProcessorCommunicator(String uniquePluginName, Context context) {
        mUniquePluginName = uniquePluginName;
        mContext = context;
        mCacheServiceCaller = new CacheServiceCaller(context);
    }

    /**
     * Processes an ExtractedText object (received from Aurora) and returns a PluginObject (or an
     * object of a subclass specific for the current plugin)
     *
     * @param extractedText The text that was extracted after Aurora's internal processing
     * @return The PluginObject that is the result of the plugin's processing of the extractedText
     */
    protected abstract PluginObject process(ExtractedText extractedText);


    /**
     * Executes the pipeline that consists of processing the ExtractedText to receive a PluginObject
     * and then caching this
     *
     * @param extractedText the text extracted by aurora
     * @return the PluginObject that is returned by {@link #process(ExtractedText)}.
     */
    @SuppressWarnings("unused")
    public final PluginObject pipeline(ExtractedText extractedText) {
        PluginObject pluginObject = process(extractedText);
        ProcessorCacheThread processorCacheThread = new ProcessorCacheThread(pluginObject,
                mCacheServiceCaller);
        processorCacheThread.start();
        return pluginObject;
    }

}

