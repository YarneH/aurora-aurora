package com.aurora.auroralib;

import com.google.gson.annotations.JsonAdapter;

import java.util.ArrayList;
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
     * The images in a section, as an {@link Image} object
     */
    private List<Image> mImages = new ArrayList<>();
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

    /**
     * The old method for getting images, instead use {@link #getImageObjects()}
     *
     * @return the list of base64 encode images
     */
    @Deprecated
    public List<String> getImages() {
        List<String> base64Images = new ArrayList<>();

        for (Image image: mImages) {
            base64Images.add(image.getBase64EncodedImage());
        }
        return base64Images;
    }

    /**
     * Get the {@link Image} objects of this Section. Will return an empty
     * list when no images are present.
     *
     * @return the list of images
     */
    @SuppressWarnings("unused")
    public List<Image> getImageObjects() {
        if (mImages != null) {
            return  mImages;
        } else {
            return new ArrayList<>();
        }
    }

    /**
     * The old method of setting images, instead use {@link #setImageObjects(List)}
     *
     * @param images List of base64 encode images
     */
    @Deprecated
    public void setImages(List<String> images) {
        mImages = new ArrayList<>();
        addImages(images);
    }

    /**
     * Set the list of {@link Image} objects
     *
     * @param images the List of {@link Image} objects
     */
    public void setImageObjects(List<Image> images) {
        this.mImages = images;
    }

    /**
     * The old method of adding images, instead use {@link #addImageObjects(List)}
     *
     * @param images List of base64 encoded images
     */
    @Deprecated
    public void addImages(List<String> images) {
        for (String image: images) {
            mImages.add(new Image(image));
        }
    }

    /**
     * Add the list of {@link Image} objects to the existing list
     *
     * @param images the List of {@link Image} objects
     */
    public void addImageObjects(List<Image> images) {
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
