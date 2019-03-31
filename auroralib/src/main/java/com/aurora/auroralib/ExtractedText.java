package com.aurora.auroralib;

import com.google.gson.Gson;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * Class to represent extracted text from an internal processor
 */
public class ExtractedText implements InternallyProcessedFile, Serializable {
    private String mFilename;
    private Date mDateLastEdit;
    private String mTitle;
    private List<String> mAuthors;
    private List<Section> mSections;

    /**
     * This constructor will create an empty extracted text
     *
     * @param filename     the name of the file
     * @param dateLastEdit the moment the file was last edited
     */
    public ExtractedText(String filename, Date dateLastEdit) {
        this.mFilename = filename;
        this.mDateLastEdit = dateLastEdit;
    }

//    public ExtractedText(String title, List<String> paragraphs) {
//        mTitle = title;
//        mParagraphs = paragraphs;
//    }

    public void addSection(Section section){
        if(mSections == null){
            mSections = new ArrayList<>();
        }
        this.mSections.add(section);
    }

    public List<Section> getSections(){
        return this.mSections;
    }

    public void addSimpleSection(String string){
        addSection(new Section(null,string,null));
    }

    public String getFilename() {
        return mFilename;
    }

    public void setFilename(String mFilename) {
        this.mFilename = mFilename;
    }

    public Date getDateLastEdit() {
        return mDateLastEdit;
    }

    public void setDateLastEdit(Date mDateLastEdit) {
        this.mDateLastEdit = mDateLastEdit;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        this.mTitle = title;
    }

//    public List<String> getParagraphs() {
//        return mParagraphs;
//    }

//    public void addParagraph(String paragraph) {
//        this.mParagraphs.add(paragraph);
//    }

    public String toString(){
        StringBuilder res = new StringBuilder();
        if (mTitle != null){
            res.append(mTitle);
        }
        if (mSections != null) {
            for (Section s : mSections) {
                res.append("\n\n").append(s.toString());
            }
        }
        return res.toString();
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
