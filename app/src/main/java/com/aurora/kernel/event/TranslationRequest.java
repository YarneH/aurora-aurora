package com.aurora.kernel.event;

/**
 * Request for the translation service. It has three fields:
 * {@link #mSentencesToTranslate} The sentences to translate
 * {@link #mSourceLanguage} The source language of the sentences in ISO-639-1 Code
 * {@link #mTargetLanguage} The target language to translate to also in ISO-639-1 Code
 */
public class TranslationRequest implements Event {

    private String[] mSentencesToTranslate;
    private String mTargetLanguage;
    private String mSourceLanguage;

    public TranslationRequest(String[] sentencesToTranslate, String sourceLanguage, String targetLanguage) {
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
