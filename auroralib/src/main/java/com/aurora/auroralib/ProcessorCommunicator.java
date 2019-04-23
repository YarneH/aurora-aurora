package com.aurora.auroralib;

import android.content.Context;

/**
 * Superclass for a 'Communicator', i.e. the interface between plugin environment and plugin processor
 */
public abstract class ProcessorCommunicator {
    protected CacheServiceCaller mCacheServiceCaller;
    protected Context mContext;

    public ProcessorCommunicator(Context context){
        mContext = context;
        mCacheServiceCaller = new CacheServiceCaller(context);
    }

    /**
     * Processes an ExtractedText object (received from Aurora) and returns a PluginObject (or an object
     * of a subclass specific for the current plugin)
     *
     *
     * @param extractedText The text that was extracted after Aurora's internal processing
     * @return The PluginObject that is the result of the plugin's processing of the extractedText
     */
    protected abstract PluginObject process(ExtractedText extractedText);

    // TODO This should not be included in final version but is still being used for testing
    protected abstract PluginObject process(String inputText);


    /**
     * IMPORTANT: use this function instead of process, this way also the cachiong operation will be executed
     * @param extractedText
     * @return
     */
    public final PluginObject pipeline(ExtractedText extractedText) {
        PluginObject pluginObject = process(extractedText);
        ProcessorCacheThread processorCacheThread = new ProcessorCacheThread(pluginObject, mCacheServiceCaller);
        processorCacheThread.start();
        return pluginObject;
    }


    public final PluginObject pipeline(String inputText) {
        //BasicPluginObject res = new BasicPluginObject();
        //res.setResult("Basic Plugin processed and cached with result:" + "\n" + extractedText.toString());

        //int cacheResult = serviceCaller.cacheOperation(res.toJSON());
        //res.setResult("Basic Plugin processed and cached with result:" + cacheResult + "\n" + extractedText.toString());

        PluginObject pluginObject = process(inputText);
        ProcessorCacheThread processorCacheThread = new ProcessorCacheThread(pluginObject, mCacheServiceCaller);
        processorCacheThread.start();
        return pluginObject;
    }
}

