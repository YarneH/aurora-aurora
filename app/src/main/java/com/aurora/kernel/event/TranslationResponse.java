package com.aurora.kernel.event;

/**
 * Response to {@link TranslationRequest}.
 * // TODO subscribe to this event
 */
public class TranslationResponse implements Event {

    /**
     * The translated sentences. Is null when {@link #getErrorMessage()} is true
     */
    private String[] mTranslatedSentences;

    /**
     * The reason why the translation failed
     */
    private String mErrorMessage;

    /**
     * Creates a translatedSentences that has error set to false
     *
     * @param translatedSentences An array with the translated strings
     */
    public TranslationResponse(String[] translatedSentences) {
        mTranslatedSentences = translatedSentences;

    }

    /**
     * Creates an error response with the errorMessage
     *
     * @param errorMessage the error to display
     */
    public TranslationResponse(String errorMessage) {
        mErrorMessage = errorMessage;

    }

    public String[] getTranslatedSentences() {
        return mTranslatedSentences;
    }

    public String getErrorMessage() {
        return mErrorMessage;
    }
}
