package com.aurora.auroralib;

import android.support.annotation.NonNull;

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
     * The images in a section, as an {@link ExtractedImage} object
     */
    private List<ExtractedImage> mExtractedImages = new ArrayList<>();
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
     * Copy constructor for a deep copy of a Section
     *
     * @param section   the Section that needs a copy
     */
    @SuppressWarnings("unused")
    public Section(final Section section) {
        // Immutable fields
        mBody = section.mBody;
        mTitle = section.mTitle;
        mLevel = section.mLevel;

        // Deep copy of objects
        ProtobufAnnotationSerializer annotationSerializer = new ProtobufAnnotationSerializer(true);
        if(mBodyAnnotationProto != null) {
            mBodyAnnotationProto = annotationSerializer.toProto(section.getBodyAnnotation());
        }
        if(mTitleAnnotationProto != null) {
            mTitleAnnotationProto = annotationSerializer.toProto(section.getTitleAnnotation());
        }

        mExtractedImages = new ArrayList<>();
        for (ExtractedImage image: section.mExtractedImages) {
            mExtractedImages.add(new ExtractedImage(image));
        }
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
     * The old method for getting images, instead use {@link #getExtractedImages()}
     *
     * @return the list of base64 encode images
     * @deprecated
     */
    @Deprecated
    public List<String> getImages() {
        List<String> base64Images = new ArrayList<>();

        for (ExtractedImage extractedImage : mExtractedImages) {
            base64Images.add(extractedImage.getBase64EncodedImage());
        }
        return base64Images;
    }

    /**
     * Get the {@link ExtractedImage} objects of this Section. Will return an empty
     * list when no images are present.
     *
     * @return the list of images
     */
    @SuppressWarnings("unused")
    public @NonNull
    List<ExtractedImage> getExtractedImages() {
        if (mExtractedImages != null) {
            return mExtractedImages;
        } else {
            return new ArrayList<>();
        }
    }

    /**
     * The old method of setting images, instead use {@link #setExtractedImages(List)}
     *
     * @param images List of base64 encode images
     * @deprecated
     */
    @Deprecated
    public void setImages(List<String> images) {
        mExtractedImages = new ArrayList<>();
        addImages(images);
    }

    /**
     * Set the list of {@link ExtractedImage} objects
     *
     * @param extractedImages the List of {@link ExtractedImage} objects
     */
    public void setExtractedImages(List<ExtractedImage> extractedImages) {
        this.mExtractedImages = extractedImages;
    }

    /**
     * The old method of adding images, instead use {@link #addExtractedImages(List)}
     *
     * @param images List of base64 encoded images
     * @deprecated
     */
    @Deprecated
    public void addImages(Iterable<String> images) {
        for (String image : images) {
            mExtractedImages.add(new ExtractedImage(image));
        }
    }

    /**
     * Adds a single {@link ExtractedImage} to this section
     *
     * @param extractedImage the ExtractedImage to add
     */
    public void addExtractedImage(ExtractedImage extractedImage) {
        if (mExtractedImages == null) {
            this.mExtractedImages = new ArrayList<>();
        }
        this.mExtractedImages.add(extractedImage);
    }

    /**
     * Add the list of {@link ExtractedImage} objects to the existing list
     *
     * @param extractedImages the List of {@link ExtractedImage} objects
     */
    public void addExtractedImages(List<ExtractedImage> extractedImages) {
        if (mExtractedImages == null) {
            this.mExtractedImages = extractedImages;
        } else {
            this.mExtractedImages.addAll(extractedImages);
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
