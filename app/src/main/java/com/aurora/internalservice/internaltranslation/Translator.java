package com.aurora.internalservice.internaltranslation;


import com.aurora.internalservice.InternalService;
import com.aurora.kernel.event.TranslationResponse;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

public class Translator implements InternalService {

    private static final String START_REQUEST = "https://translation.googleapis.com/language/translate/v2?";
    private static final String QUERY = "&q=";
    private static final String TARGET = "&target=";
    private static final String SOURCE = "&source=";


    private static final String API_KEY = "{YOUR_API_KEY}";
    //TODO get an API key and hide this in the app
    private static final String KEY = "&key=" + API_KEY;


    public Translator() {
        // static elements

    }

    /**
     * Makes the url to use google translate API
     *
     * @param sentencesToTranslate The sentences to translate
     * @param sourceLanguage       the source language
     * @param targetLanguage       the target language
     * @return The url to access
     * @throws UnsupportedEncodingException if the sentences cannot be encoded into url
     */
    public String makeUrl(String[] sentencesToTranslate, String sourceLanguage, String targetLanguage)
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
     *
     * @param response The response from google api
     * @return a translation response that contains the translated sentenced
     * @throws JSONException An exception that signifies this object is not according to the google translate
     *                       api
     */
    @java.lang.SuppressWarnings("squid:CommentedOutCodeLine")
    public TranslationResponse getTranslationRespons(JsonObject response) throws JSONException {
        // TODO get the translations
        // see https://developers.google.com/apis-explorer/?hl=nl#p/translate/v2/language.
        // translations.list?q=this+is+a+test&q=a+test+with+multiple+values&q=that+is+a+lot+of+values
        // &q=wow+so+many+values+&q=%252B%252B%252B%252B%252B%252B&q=%252F&target=nl&source=en&_h=4&
        // I don't completely know how the response will look like something like:

        // {
        // "data": {
        //  "translations": [
        //   {
        //    "translatedText": "dit is een test"
        //   },
        //   {
        //    "translatedText": "wat een gave test"
        //   },
        //   {
        //    "translatedText": "met escape-tekens / +."
        //   },
        //   {
        //    "translatedText": "hoe vertaalt dat"
        //   }
        //  ]
        // }
        //}
        // but cannot test before the API is acquired
        JsonArray jsonTranslations = response.getAsJsonObject("data").getAsJsonArray("translations");
        String[] translations = new String[jsonTranslations.size()];
        for (int i = 0; i < jsonTranslations.size(); i++) {
            String translation = jsonTranslations.get(i).getAsJsonObject().
                    getAsJsonPrimitive("translatedText").getAsString();

            translations[i] = (translation);
        }
        return new TranslationResponse(translations);


    }
}
