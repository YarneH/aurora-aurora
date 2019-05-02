package com.aurora.auroralib.translation;

import android.util.Log;

import com.aurora.auroralib.PluginObject;
import com.aurora.auroralib.cache.CacheResults;
import com.aurora.auroralib.cache.CacheServiceCaller;
import com.aurora.auroralib.cache.ProcessorCacheThread;

import java.util.List;

//TODO
public class ProcessorTranslationThread extends Thread {
    /**
     * Tag used for logging
     */
    private static final String TAG = ProcessorTranslationThread.class.getSimpleName();

    /**
     * Result of translation operation
     */
    protected List<String> mTranslatedSentences = null;

    /**
     * List of strings to be translated
     */
    protected List<String> mSentences;

    /**
     * Source language in ISO format
     */
    protected String mSourceLanguage;

    /**
     * Destination language in ISO format
     */
    protected String mDestinationLanguage;


    /**
     * a reference to the translation service caller that is responsible for actually calling the cache
     */
    private TranslationServiceCaller mTranslationServiceCaller;

    public ProcessorTranslationThread(List<String> sentences,
                                      String sourceLanguage,
                                      String destinationLanguage,
                                      TranslationServiceCaller translationServiceCaller) {
        this.mSentences = sentences;
        this.mSourceLanguage = sourceLanguage;
        this.mDestinationLanguage = destinationLanguage;
        this.mTranslationServiceCaller = translationServiceCaller;
    }

    public List<String> getTranslatedSentences() {
        return mTranslatedSentences;
    }

    protected List<String> translate() {
        return mTranslationServiceCaller.translateOperation(mSentences, mSourceLanguage, mDestinationLanguage);
    }

    /**
     * This method can be overridden to take particular actions depending on the cache result.
     * Currently tries caching again if it failed during its first attempt.
     *
     * @param cacheResult result of the first caching operation. 0 means success.
     */
    /*
    protected void handleCacheResult(int cacheResult) {
        if (cacheResult != 0) {
            int secondResult = cache();
            Log.d(TAG, "Second cache operation result: " + secondResult);
        }
    }
    */

    @Override
    public void run() {
        mTranslatedSentences = translate();
        Log.d(TAG, "" + mTranslatedSentences);
        //handleCacheResult(cacheResult);
    }
}
