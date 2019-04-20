package com.aurora.kernel.event;

/**
 * Request for the translation service
 * TODO post this event
 */
public class TranslationRequest implements Event {

    private String[] mSentencesToTranslate;
    private String mTargetLanguage;
    private String mSourceLanguage;

    public TranslationRequest(String[] sentencesToTranslate, String targetLanguage, String sourceLanguage) {
        mSentencesToTranslate = sentencesToTranslate;
        mTargetLanguage = targetLanguage;
        mSourceLanguage = sourceLanguage;
    }

    public String[] getSentencesToTranslate() {
        return mSentencesToTranslate;
    }

    public String getTargetLanguage() {
        return mTargetLanguage;
    }

    public String getSourceLanguage() {
        return mSourceLanguage;
    }
}
