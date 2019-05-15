package com.aurora.auroralib;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.gson.annotations.JsonAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.CoreNLPProtos;
import edu.stanford.nlp.pipeline.ProtobufAnnotationSerializer;

/**
 * Object representing an section extracted from files. Sections are the main type of objects in
 * {@link ExtractedText}. A section can contain a title and a body, both of which can have a
 * CoreNLP {@link Annotation}. A section can also contain a List of {@link ExtractedImage}.
 * Lastly a Section has a level, an int representing the depth at which this Section occurs in
 * the flat {@link ExtractedText}.
 */
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
    private int mLevel = 0;

    /**
     * Constructor for creating an empty section
     */
    public Section() {

    }

    /**
     * Constructor for creating a section without images or title
     *
     * @param body NonNull content of the section
     */
    public Section(@NonNull final String body) {
        this.mBody = body;
    }

    /**
     * Copy constructor for a deep copy of a Section
     *
     * @param section the Section that needs a copy
     */
    @SuppressWarnings("unused")
    public Section(@NonNull final Section section) {
        // Immutable fields
        mBody = section.mBody;
        mTitle = section.mTitle;
        mLevel = section.mLevel;

        // Deep copy of objects
        ProtobufAnnotationSerializer annotationSerializer = new ProtobufAnnotationSerializer(true);
        if(section.getBodyAnnotation() != null) {
            mBodyAnnotationProto = annotationSerializer.toProto(section.getBodyAnnotation());
        }
        if(section.getTitleAnnotation() != null) {
            mTitleAnnotationProto = annotationSerializer.toProto(section.getTitleAnnotation());
        }

        mExtractedImages = new ArrayList<>();
        for (ExtractedImage image : section.mExtractedImages) {
            mExtractedImages.add(new ExtractedImage(image));
        }
    }

    @NonNull
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

    /**
     * @return NonNull title of the Section
     */
    @NonNull
    public String getTitle() {
        if (mTitle == null) {
            return "";
        }
        return mTitle;
    }

    /**
     * Sets the title of the section.
     *
     * @param title NonNull title
     */
    public void setTitle(@NonNull final String title) {
        mTitle = title;
    }

    /**
     * Sets the {@link Annotation} for the title as a Protobuf object generated by
     * {@link ProtobufAnnotationSerializer} that is Serializable
     *
     * @param titleAnnotation protobuf object of the Annotation
     */
    public void setTitleAnnotationProto(@Nullable final CoreNLPProtos.Document titleAnnotation) {
        mTitleAnnotationProto = titleAnnotation;
    }

    /**
     * @return the {@link Annotation} set in the {@link #mTitleAnnotationProto}.
     */
    @SuppressWarnings("unused")
    @Nullable
    public Annotation getTitleAnnotation() {
        // Recover the title CoreNLP annotations
        if (mTitleAnnotationProto != null && mTitleAnnotation == null) {
            ProtobufAnnotationSerializer annotationSerializer =
                    new ProtobufAnnotationSerializer(true);
            mTitleAnnotation = annotationSerializer.fromProto(mTitleAnnotationProto);
        }

        return mTitleAnnotation;
    }

    /**
     * @return the NonNull body of the Section
     */
    @NonNull
    public String getBody() {
        if (mBody == null) {
            return "";
        }
        return mBody;
    }

    /**
     * Sets the body of the section.
     *
     * @param body NonNull body
     */
    @SuppressWarnings("unused")
    public void setBody(@NonNull final String body) {
        mBody = body;
    }

    /**
     * Concat the body of the section.
     *
     * @param body NonNull body
     */
    public void concatBody(@NonNull final String body) {
        if (mBody == null) {
            mBody = body;
        } else {
            mBody = mBody.concat(body);
        }
    }

    /**
     * Sets the {@link Annotation} for the body as a Protobuf object generated by
     * {@link ProtobufAnnotationSerializer} that is Serializable
     *
     * @param bodyAnnotationProto protobuf object of the Annotation
     */
    public void setBodyAnnotationProto(@Nullable final CoreNLPProtos.Document bodyAnnotationProto) {
        mBodyAnnotationProto = bodyAnnotationProto;
    }

    /**
     * @return the {@link Annotation} set in the {@link #mBodyAnnotationProto}.
     */
    @SuppressWarnings("unused")
    @Nullable
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
    @NonNull
    public List<String> getImages() {
        List<String> base64Images = new ArrayList<>();

        for (ExtractedImage extractedImage : mExtractedImages) {
            base64Images.add(extractedImage.getBase64EncodedImage());
        }
        return base64Images;
    }

    /**
     * The old method of setting images, instead use {@link #setExtractedImages(List)}
     *
     * @param images List of base64 encode images
     * @deprecated
     */
    @Deprecated
    public void setImages(@NonNull final List<String> images) {
        mExtractedImages = new ArrayList<>();
        addImages(images);
    }

    /**
     * Get the {@link ExtractedImage} objects of this Section. Will return an empty
     * list when no images are present.
     *
     * @return the list of images
     */
    @SuppressWarnings("unused")
    @NonNull
    public List<ExtractedImage> getExtractedImages() {
        if (mExtractedImages == null) {
            return new ArrayList<>();
        }
        return mExtractedImages;
    }

    /**
     * Set the list of {@link ExtractedImage} objects
     *
     * @param extractedImages the NonNull List of {@link ExtractedImage} objects
     */
    public void setExtractedImages(@NonNull final List<ExtractedImage> extractedImages) {
        mExtractedImages = extractedImages;
    }

    /**
     * The old method of adding images, instead use {@link #addExtractedImages(List)}
     *
     * @param images List of base64 encoded images
     * @deprecated
     */
    @Deprecated
    public void addImages(@NonNull final Iterable<String> images) {
        for (String image : images) {
            mExtractedImages.add(new ExtractedImage(image));
        }
    }

    /**
     * Adds a single {@link ExtractedImage} to this section
     *
     * @param extractedImage the NonNull ExtractedImage to add
     */
    public void addExtractedImage(@NonNull final ExtractedImage extractedImage) {
        if (mExtractedImages == null) {
            mExtractedImages = new ArrayList<>();
        }
        mExtractedImages.add(extractedImage);
    }

    /**
     * Add the list of {@link ExtractedImage} objects to the existing list
     *
     * @param extractedImages the NonNull List of {@link ExtractedImage} objects
     */
    public void addExtractedImages(@NonNull final List<ExtractedImage> extractedImages) {
        if (mExtractedImages == null) {
            mExtractedImages = extractedImages;
        } else {
            mExtractedImages.addAll(extractedImages);
        }
    }

    /**
     * @return the level at which the Section was extracted from the file. Default value is 0.
     */
    @SuppressWarnings("unused")
    public int getLevel() {
        return mLevel;
    }

    /**
     * Sets the level at which the Section was extracted from the file. Default value is 0.
     *
     * @param level the level of the Section
     */
    public void setLevel(int level) {
        mLevel = level;
    }
}
