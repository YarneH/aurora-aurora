package com.aurora.kernel.event;

/**
 * Response to {@link TranslationRequest}.
 * // TODO subscribe to this event
 */
public class TranslationResponse implements Event {

    /**
     * The translated sentences. Is null when {@link #isError()} is true
     */
    private String[] mTranslatedSentences;
    /**
     * Indicates wheter the translation failed
     */
    private boolean mError;
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
        mError = false;
    }

    /**
     * Creates an error response with the errorMessage
     *
     * @param errorMessage the error to display
     */
    public TranslationResponse(String errorMessage) {
        mErrorMessage = errorMessage;
        mError = true;
    }

    public String[] getTranslatedSentences() {
        return mTranslatedSentences;
    }

    public boolean isError() {
        return mError;
    }
}
