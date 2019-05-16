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
        if (section.getBodyAnnotation() != null) {
            mBodyAnnotationProto = annotationSerializer.toProto(section.getBodyAnnotation());
        }
        if (section.getTitleAnnotation() != null) {
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
        StringBuilder result = new StringBuilder();

        if (mTitle != null && !mTitle.isEmpty()) {
            result.append(mTitle);
            result.append("\n");
        }
        if (mBody != null && !mBody.isEmpty()) {
            result.append(mBody);
            result.append("\n");
        }

        return result.toString().trim();
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
    @SuppressWarnings({"unused", "WeakerAccess"})
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

    @Override
    public boolean equals(Object o) {

        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Section other = (Section) o;

        boolean equals;

        equals = this.getTitle().equals(other.getTitle());

        if (this.getTitleAnnotation() != null && other.getTitleAnnotation() != null) {
            equals &= this.getTitleAnnotation().equals(other.getTitleAnnotation());
        }
        equals &= this.getBody().equals(other.getBody());

        if (this.getBodyAnnotation() != null && other.getBodyAnnotation() != null) {
            equals &= this.getBodyAnnotation().equals(other.getBodyAnnotation());
        }
        equals &= this.getExtractedImages().equals(other.getExtractedImages());
        equals &= this.getLevel() == other.getLevel();

        return equals;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getTitle(), getTitleAnnotation(), getBody(), getBodyAnnotation(),
                getExtractedImages(), getLevel());
    }
}
