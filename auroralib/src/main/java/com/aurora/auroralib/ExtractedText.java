package com.aurora.auroralib;

import com.google.gson.Gson;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/**
 * Class to represent extracted text from an internal processor
 */
public class ExtractedText implements InternallyProcessedFile, Serializable {
    private String mTitle;
    private List<String> mParagraphs;

    public ExtractedText(String title, List<String> paragraphs) {
        mTitle = title;
        mParagraphs = paragraphs;
    }

    public ExtractedText() {
        mTitle = null;
        mParagraphs = new ArrayList<>();
    }


    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        this.mTitle = title;
    }

    public List<String> getParagraphs() {
        return mParagraphs;
    }

    public void addParagraph(String paragraph) {
        this.mParagraphs.add(paragraph);
    }

    public String toString(){
        String res = "";
        if (mTitle != null){
            res += mTitle;
        }
        if (mParagraphs != null) {
            for (String s : mParagraphs) {
                res += "\n\n" + s;
            }
        }
        return res;
    }

    /**
     * Turns the extracted text to a JSON string for easy passing to plugin.
     *
     * @return String (in JSON format)
     */
    public String toJSON(){
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    /**
     * Turn the JSON string back into an ExtractedText object, mainly for use by plugins.
     *
     * @param json  The extracted JSON string of the ExtractedText object
     * @return ExtractedText
     */
    public static ExtractedText fromJson(String json){
        Gson gson = new Gson();
        return gson.fromJson(json, ExtractedText.class);
    }
}
