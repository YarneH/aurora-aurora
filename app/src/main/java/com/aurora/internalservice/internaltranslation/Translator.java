package com.aurora.internalservice.internaltranslation;


import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.aurora.aurora.BuildConfig;
import com.aurora.internalservice.InternalService;
import com.aurora.kernel.Bus;
import com.aurora.kernel.event.TranslationRequest;
import com.aurora.kernel.event.TranslationResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

public final class Translator implements InternalService {

    /**
     * A queue to post http requests to
     */
    private RequestQueue mRequestQueue;

    /**
     * The unique bus
     */
    private Bus mBus;

    /**
     * A static variable for accessing the google API service, it is the beginning of the url that
     * translates the added queries
     */
    private static final String HTTP_BEGIN = "https://translation.googleapis.com/language/translate/v2?";
    /**
     * Beginning tag for a query in the url to send to the API
     */
    private static final String QUERY = "&q=";
    /**
     * Beginning tag for the target language in the url to send to the API
     */
    private static final String TARGET = "&target=";

    /**
     * Beginning tag for the target language in the url to send to the API
     */
    private static final String SOURCE = "&source=";

    /**
     * Beginning tag and key for in the url to send to the API
     */
    private static final String KEY = "&key=" +BuildConfig.TRANSLATION_API;


    public Translator(Bus bus, RequestQueue requestQueue) {
        mBus = bus;
        mRequestQueue = requestQueue;


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
        StringBuilder bld = new StringBuilder(HTTP_BEGIN);
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
     * @return a translation response that contains the translated sentences
     * @throws JSONException An exception that signifies this object is not according to the google translate
     *                       api
     */
    private static TranslationResponse getTranslationResponse(JSONObject response) throws JSONException {

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

    /**
     * private helper method for when a {@link TranslationRequest} comes in. It calls
     * {@link Translator#makeUrl(String[], String, String)} to get a url and posts this to the {@link #mRequestQueue}
     *
     * @param sentencesToTranslate the sentences to translate
     * @param sourceLanguage       language to translate from
     * @param targetLanguage       language to translate to
     */
    public void translate(String[] sentencesToTranslate, String sourceLanguage, String targetLanguage) {
        try {
            String url = makeUrl(sentencesToTranslate, sourceLanguage, targetLanguage);
            Log.d("TRANSLATE", url);
            // Request a json response from the provided URL.
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url,
                    null, this::postTranslationResponseEvent, this::postTranslationResponseEvent);
            jsonObjectRequest.setTag("TRANSLATOR");

            mRequestQueue.add(jsonObjectRequest);
            Log.d("TRANSLATE", "request added");
        } catch (IOException e) {
            Log.e("TRANSLATION", "Translation failed", e);
            postTranslationResponseEvent(e);
        }
    }

    /**
     * Posts a {@link TranslationResponse} event getting the translated data from the argument
     *
     * @param response the response from the {@link RequestQueue} to the Google API
     */
    private void postTranslationResponseEvent(JSONObject response) {
        try {
            mBus.post(Translator.getTranslationResponse(response));

        } catch (JSONException e) {
            Log.e("JSON", "getting from json failed", e);
            postTranslationResponseEvent(e);
        }


    }

    /**
     * Posts a {@link TranslationResponse} that signifies the transation failed
     *
     * @param error the reason why the translation failed
     */
    private void postTranslationResponseEvent(Exception error) {
        TranslationResponse errorResponse = new TranslationResponse(error.getMessage());
        mBus.post(errorResponse);
    }
}
