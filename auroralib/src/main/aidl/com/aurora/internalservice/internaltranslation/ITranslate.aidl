// ITranslate.aidl
package com.aurora.internalservice.internaltranslation;

// Declare any non-default types here with import statements
//import com.aurora.auroralib.internalservice.internaltranslation.TranslationResult;

interface ITranslate {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */

    //TranslationResult translate(in List<String> sentences, String sourceLanguage, String destinationLanguage);
    List<String> translate(in List<String> sentences, String sourceLanguage, String destinationLanguage);
}
