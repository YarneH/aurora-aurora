package com.aurora.auroralib;

import com.google.gson.annotations.JsonAdapter;

import java.util.List;

import edu.stanford.nlp.pipeline.Annotation;
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
    private CoreNLPProtos.Document mTitleAnnotationProto;
    /**
     * The deserialized Annotation of the Section title
     */
    private Annotation mTitleAnnotation;

    /**
     * The content/body of a Section (the text)
     */
    private String mBody;
    /**
     * The CoreNLP annotations of the Section body in Google's protobuf format.
     */
    @JsonAdapter(CoreNLPDocumentAdapter.class)
    private CoreNLPProtos.Document mBodyAnnotationProto;
    /**
     * The deserialized Annotation of the Section body
     */
    private Annotation mBodyAnnotation;

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

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        this.mTitle = title;
    }

    CoreNLPProtos.Document getTitleAnnotationProto() {
        return mTitleAnnotationProto;
    }

    public void setTitleAnnotationProto(CoreNLPProtos.Document titleAnnotations) {
        this.mTitleAnnotationProto = titleAnnotations;
    }

    @SuppressWarnings("unused")
    public Annotation getTitleAnnotation() {
        return mTitleAnnotation;
    }

    void setTitleAnnotation(Annotation annotation) {
        this.mTitleAnnotation = annotation;
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

    CoreNLPProtos.Document getBodyAnnotationProto() {
        return mBodyAnnotationProto;
    }

    public void setBodyAnnotationProto(CoreNLPProtos.Document bodyAnnotationProto) {
        this.mBodyAnnotationProto = bodyAnnotationProto;
    }

    @SuppressWarnings("unused")
    public Annotation getBodyAnnotation() {
        return mBodyAnnotation;
    }

    void setBodyAnnotation(Annotation mBodyAnnotation) {
        this.mBodyAnnotation = mBodyAnnotation;
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

    @SuppressWarnings("unused")
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
