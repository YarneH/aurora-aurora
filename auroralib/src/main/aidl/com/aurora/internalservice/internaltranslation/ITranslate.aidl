// ITranslate.aidl
package com.aurora.internalservice.internaltranslation;

/**
 * AIDL interface for the translation operation
 */
interface ITranslate {
    /**
     * Translates a list of sentences from the sourceLanguage to the destinationLanguage
     */
    List<String> translate(in List<String> sentences, String sourceLanguage, String destinationLanguage);
}
