package com.aurora.auroralib.translation;

/**
 * Exception to be thrown if a provided sentence to the translation request is too long for a safe
 * request
 */
public class TranslationSentenceTooLongException extends Exception {
    private static final String MSG = "Translation sentence too long: ";
    private final String mSentence;

    public TranslationSentenceTooLongException(String sentence){
        this.mSentence = sentence;

    }

    @Override
    public String getMessage(){
        return MSG + mSentence;
    }

}
