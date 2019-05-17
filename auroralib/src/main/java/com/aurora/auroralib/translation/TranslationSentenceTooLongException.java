package com.aurora.auroralib.translation;

/**
 * Exception to be thrown if a provided sentence to the translation request is too long for a safe
 * request
 */
public class TranslationSentenceTooLongException extends Exception {
    private static final String MSG = "Translation sentence too long: ";
    private final String mSentence;

    /**
     * Constructor of the exception
     *
     * @param sentence  Sentence that was too long for the translation service
     */
    public TranslationSentenceTooLongException(String sentence){
        this.mSentence = sentence;

    }

    /**
     * Gets the full errors message
     *
     * @return The standard MSG followed by the sentence that was too long
     */
    @Override
    public String getMessage(){
        return MSG + mSentence;
    }

}
