package com.aurora.internalservice.internaltranslation;


import com.aurora.aurora.BuildConfig;
import com.aurora.internalservice.InternalService;
import com.aurora.kernel.event.TranslationResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

public class Translator implements InternalService {

    private static final String START_REQUEST = "https://translation.googleapis.com/language/translate/v2?";
    private static final String QUERY = "&q=";
    private static final String TARGET = "&target=";
    private static final String SOURCE = "&source=";

    private static final String API_KEY = BuildConfig.TRANSLATION_API;

    private static final String KEY = "&key=" + API_KEY;


    public Translator() {
        // static elements
    }

    /**
     * Makes the url to use google translate API
     *
     * @param sentencesToTranslate The sentences to translate
     * @param sourceLanguage       the source language (ISO-639-1 Code)
     * @param targetLanguage       the target language (ISO-639-1 Code)
     * @return The url to access
     * @throws UnsupportedEncodingException if the sentences cannot be encoded into url
     */
    public static String makeUrl(String[] sentencesToTranslate, String sourceLanguage, String targetLanguage)
            throws UnsupportedEncodingException {
        StringBuilder bld = new StringBuilder(START_REQUEST);
        boolean first = true;
        for (String sentence : sentencesToTranslate) {
            //add start of query
            if (first) {
                // the first addititon should escape the &
                bld.append(QUERY.substring(1));
                first = false;
            } else {
                bld.append(QUERY);
            }
            // add the converted sentence so that escape characters are correct
            bld.append(java.net.URLEncoder.encode(sentence, "ISO-8859-1"));
        }
        // append the target
        bld.append(TARGET).append(targetLanguage);
        // append the source
        bld.append(SOURCE).append(sourceLanguage);
        // append the key
        bld.append(KEY);

        return bld.toString();
    }


    /**
     * Gets the translation from the JSON object using google api"s standard
     * <p>
     * The JSONObject is structured as follows: {"data": {"translations" : {"translatedText": return of translation}}}
     *
     * @param response The response from google api
     * @return a translation response that contains the translated sentenced
     * @throws JSONException An exception that signifies this object is not according to the google translate
     *                       api
     */
    public static TranslationResponse getTranslationResponse(JSONObject response) throws JSONException {

        // Get the translations
        JSONArray jsonTranslations = response.getJSONObject("data").getJSONArray("translations");
        // Create an array for storing the translations
        String[] translations = new String[jsonTranslations.length()];
        // Store the translatedText
        for (int i = 0; i < jsonTranslations.length(); i++) {
            String translation = jsonTranslations.getJSONObject(i).getString("translatedText");
            translations[i] = (translation);
        }
        // Return the response
        return new TranslationResponse(translations);


    }
}
