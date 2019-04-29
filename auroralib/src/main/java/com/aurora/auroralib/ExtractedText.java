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
    // Necessary to enable serialization during class evolution
    // wiki.sei.cmu.edu/confluence/display/java/SER00-J.+Enable+serialization+compatibility+during+class+evolution
    private static final long serialVersionUID = 1L;

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

    /**
     * This constructor will create an empty extracted text
     *
     * @param filename     the name of the file
     * @param dateLastEdit the moment the file was last edited
     * @param sections     the sections in the file (only plain sections)
     */
    public ExtractedText(String filename, Date dateLastEdit, List<String> sections) {
        mFilename = filename;
        mDateLastEdit = dateLastEdit;

        mSections = new ArrayList<>();

        // Do not use overridable method because this can lead to strange behaviour in subclasses
        // wiki.sei.cmu.edu/confluence/display/java/MET05-J.+Ensure+that+constructors+do+not+call+overridable+methods
        for (String section : sections) {
            mSections.add(new Section(section));
        }
    }

    /**
     * Constructor for an extracted text with all arguments
     *
     * @param mFilename     the name of the file
     * @param mDateLastEdit the moment the file was last edited
     * @param mTitle        the title of the file
     * @param mAuthors      the authors of the file
     * @param mSections     the sections in the file
     */
    public ExtractedText(String mFilename, Date mDateLastEdit, String mTitle, List<String> mAuthors,
                         List<Section> mSections) {
        this(mFilename, mDateLastEdit);
        this.mTitle = mTitle;
        this.mAuthors = mAuthors;
        this.mSections = mSections;
    }

    /**
     * Get the sections of this ExtractedText
     *
     * @return the list of sections
     */
    public List<Section> getSections() {
        return this.mSections;
    }

    /**
     * Adds the section to the list of sections for this extractedText
     *
     * @param section the section to be added
     */
    public void addSection(Section section) {
        if (mSections == null) {
            mSections = new ArrayList<>();
        }
        this.mSections.add(section);
    }

    /**
     * Adds a new section without images or title
     *
     * @param sectionText the content of the section
     */
    public void addSimpleSection(String sectionText) {
        addSection(new Section(sectionText));
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


    public String toString() {
        StringBuilder res = new StringBuilder();
        if (mTitle != null) {
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
    public String toJSON() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    /**
     * Turn the JSON string back into an ExtractedText object, mainly for use by plugins.
     *
     * @param json The extracted JSON string of the ExtractedText object
     * @return ExtractedText
     */
    public static ExtractedText fromJson(String json) {
        Gson gson = new Gson();
        return gson.fromJson(json, ExtractedText.class);
    }
}
