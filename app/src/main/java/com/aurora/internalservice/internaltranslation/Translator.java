package com.aurora.internalservice.internaltranslation;


import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.aurora.aurora.BuildConfig;
import com.aurora.internalservice.InternalService;
import com.aurora.kernel.event.TranslationRequest;
import com.aurora.kernel.event.TranslationResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

public final class Translator implements InternalService {

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
    private static final String KEY = "&key=" + BuildConfig.TRANSLATION_API;
    private final Object lock = new Object();
    /**
     * A queue to post http requests to
     */
    private RequestQueue mRequestQueue;

    /**
     * The most recent mInternalResponse received from the queue
     */
    private TranslationResponse mInternalResponse;

    public Translator(RequestQueue requestQueue) {

        mRequestQueue = requestQueue;

    }

    /**
     * This method translates a request using google's Translation API it returns the response
     * to this request
     *
     * @param request the translationRequest
     * @return The response to the request
     */
    public TranslationResponse translate(TranslationRequest request) {

        try {
            String url = makeUrl(request.getSentencesToTranslate(), request.getSourceLanguage(),
                    request.getTargetLanguage());
            Log.d("TRANSLATE", url);
            // Request a json mInternalResponse from the provided URL.
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url,
                    null, this::evaluateResponse, this::evaluateResponse);
            jsonObjectRequest.setTag("TRANSLATOR");

            mRequestQueue.add(jsonObjectRequest);
            Log.d("TRANSLATE", "request added");
        } catch (IOException e) {
            Log.e("TRANSLATION", "Translation failed", e);
            evaluateResponse(e);
        }
        // either the request is added or an error response will be created so wait untill a response
        // is made
        synchronized (lock) {
            while (mInternalResponse == null) {
                try {
                    lock.wait();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
            // copy the internal response
            TranslationResponse result = mInternalResponse;
            // set the internalresponse back to null
            mInternalResponse = null;
            // return the copy
            return result;
        }

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
     * Create a TranslationResponse event from the JSONObject
     *
     * @param response the json response from google's API
     */
    private void evaluateResponse(JSONObject response) {

        try {
            synchronized (lock) {
                this.mInternalResponse = Translator.getTranslationResponse(response);
                lock.notifyAll();
            }
        } catch (JSONException e) {
            Log.e("JSON", "getting from json failed", e);
            evaluateResponse(e);
        }

    }


    /**
     * Creates a Translation response that signifies something went wrong
     *
     * @param error the reason why the translation failed
     */
    private void evaluateResponse(Exception error) {
        synchronized (lock) {
            mInternalResponse = new TranslationResponse(error.getMessage());
            lock.notifyAll();
        }
    }


    /**
     * Gets the translation from the JSON object using google api"s standard
     * <p>
     * The JSONObject is structured as follows: {"data": {"translations" : {"translatedText": return of translation}}}
     *
     * @param response The mInternalResponse from google api
     * @return a translation mInternalResponse that contains the translated sentences
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
        // Return the mInternalResponse
        return new TranslationResponse(translations);

    }
}
