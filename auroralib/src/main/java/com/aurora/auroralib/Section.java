package com.aurora.auroralib;

import com.google.gson.annotations.JsonAdapter;

import java.util.List;

import edu.stanford.nlp.pipeline.CoreNLPProtos;

public class Section {

    /**
     * The title of a Section
     */
    private String mTitle;
    /**
     * The CoreNLP annotations of the Section title in Google's protobuf format.
     */
    @JsonAdapter(CoreNLPDocumentAdapter.class)
    private CoreNLPProtos.Document mTitleAnnotations;
    /**
     * The content/body of a Section (the text)
     */
    private String mBody;
    /**
     * The CoreNLP annotations of the Section body in Google's protobuf format.
     */
    @JsonAdapter(CoreNLPDocumentAdapter.class)
    private CoreNLPProtos.Document mBodyAnnotations;
    /**
     * The images in a section, as a Base64 String
     */
    private List<String> mImages;
    /**
     * The level of the section, default level is 0
     */
    private int mLevel;

    /**
     * Constructor for creating an empty section
     */
    public Section() {

    }

    /**
     * Constructor for creating a section without images or title
     *
     * @param body the content of the section
     */
    public Section(String body) {
        this.mBody = body;
    }

    /**
     * Constructor for creating a section with a title, content and images
     *
     * @param title  the title of the section
     * @param body   the content of the section
     * @param images the images in the section
     */
    public Section(String title, String body, List<String> images) {
        this.mTitle = title;
        this.mBody = body;
        this.mImages = images;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        this.mTitle = title;
    }

    public CoreNLPProtos.Document getTitleAnnotations() {
        return mTitleAnnotations;
    }

    public void setTitleAnnotations(CoreNLPProtos.Document titleAnnotations) {
        this.mTitleAnnotations = titleAnnotations;
    }

    public String getBody() {
        return mBody;
    }

    public void setBody(String body) {
        this.mBody = body;
    }

    public void concatBody(String body) {
        if (mBody == null) {
            this.mBody = body;
        } else {
            this.mBody = this.mBody.concat(body);
        }
    }

    public CoreNLPProtos.Document getBodyAnnotations() {
        return mBodyAnnotations;
    }

    public void setBodyAnnotations(CoreNLPProtos.Document bodyAnnotations) {
        this.mBodyAnnotations = bodyAnnotations;
    }

    public List<String> getImages() {
        return mImages;
    }

    public void setImages(List<String> images) {
        this.mImages = images;
    }

    public void addImages(List<String> images) {
        if (mImages == null) {
            this.mImages = images;
        } else {
            this.mImages.addAll(images);
        }
    }

    public int getLevel() {
        return mLevel;
    }

    public void setLevel(int level) {
        this.mLevel = level;
    }

    @Override
    public String toString() {
        if (mTitle != null && mBody != null) {
            return mTitle + "\n" + mBody + "\n";
        } else if (mBody != null) {
            return mBody + "\n";
        } else if (mTitle != null) {
            return (mTitle + "\n");
        } else return "Empty paragraph.\n";
    }
}
