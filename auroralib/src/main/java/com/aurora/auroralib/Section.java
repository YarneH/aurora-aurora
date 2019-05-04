package com.aurora.auroralib;

import com.google.gson.annotations.JsonAdapter;

import java.util.List;

import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.CoreNLPProtos;
import edu.stanford.nlp.pipeline.ProtobufAnnotationSerializer;

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
    private transient Annotation mTitleAnnotation;

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
    private transient Annotation mBodyAnnotation;

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

    @Override
    public String toString() {
        String result;
        if (mTitle != null && mBody != null) {
            result = mTitle + "\n" + mBody + "\n";
        } else if (mBody != null) {
            result = mBody + "\n";
        } else if (mTitle != null) {
            result = (mTitle + "\n");
        } else {
            result = "Empty paragraph.\n";
        }

        return result;
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
        // Recover the title CoreNLP annotations
        if (mTitleAnnotationProto != null && mTitleAnnotation == null) {
            ProtobufAnnotationSerializer annotationSerializer =
                    new ProtobufAnnotationSerializer(true);
            mTitleAnnotation = annotationSerializer.fromProto(mTitleAnnotationProto);
        }

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
        // Recover the body CoreNLP annotations
        if (mBodyAnnotationProto != null && mBodyAnnotation == null) {
            ProtobufAnnotationSerializer annotationSerializer =
                    new ProtobufAnnotationSerializer(true);
            mBodyAnnotation = annotationSerializer.fromProto(mBodyAnnotationProto);
        }

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
}
