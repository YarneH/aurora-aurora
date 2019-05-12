package com.aurora.auroralib.translation;

import android.util.Log;

import java.util.List;

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

    @Override
    public void run() {
        mTranslatedSentences = translate();
        Log.d(TAG, "" + mTranslatedSentences);
        // Maybe use something like handleCacheResult()
    }
}
