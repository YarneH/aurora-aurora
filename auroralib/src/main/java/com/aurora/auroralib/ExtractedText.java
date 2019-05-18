package com.aurora.auroralib;

import android.content.Context;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.annotations.JsonAdapter;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.CoreNLPProtos;
import edu.stanford.nlp.pipeline.ProtobufAnnotationSerializer;


/**
 * This class represents everything that could be extracted from a file by Aurora. It contains a
 * file unique identifier {@link #mFilename}, a Title, a list of authors and
 * a list of {@link Section}.
 */
public class ExtractedText implements Serializable {

    /**
     * The filename, which is made unique with a prepended hash
     */
    private String mFilename;

    /**
     * The text of the title of the file
     */
    private String mTitle;

    /**
     * The CoreNLP annotations of the title in Google's protobuf format
     */
    @JsonAdapter(CoreNLPDocumentAdapter.class)
    private CoreNLPProtos.Document mTitleAnnotationProto;

    /**
     * The deserialized Annotation of the title
     */
    private transient Annotation mTitleAnnotation;

    /**
     * A list of authors of the file
     */
    private List<String> mAuthors;

    /**
     * A list of Sections of the file which represent the content
     */
    private List<Section> mSections;

    /**
     * Default constructor for creating an empty extracted text
     *
     * @param filename the name of the file
     */
    public ExtractedText(@NonNull final String filename) {
        mFilename = filename;
    }

    /**
     * This constructor will create an empty extracted text
     *
     * @param filename the name of the file
     * @param sections the sections in the file (only plain sections)
     */
    public ExtractedText(@NonNull final String filename, @NonNull final List<String> sections) {
        mFilename = filename;

        mSections = new ArrayList<>();

        // Do not use overridable method because this can lead to strange behaviour in subclasses
        // wiki.sei.cmu.edu/confluence/display/java/MET05-J
        // .+Ensure+that+constructors+do+not+call+overridable+methods
        for (String section : sections) {
            mSections.add(new Section(section));
        }
    }

    /**
     * Turn the JSON string back into an ExtractedText object, mainly for use by plugins.
     *
     * @param json The extracted JSON string of the ExtractedText object
     * @return ExtractedText
     */
    @SuppressWarnings({"unused", "WeakerAccess"})
    public static ExtractedText fromJson(@NonNull final String json) {
        Gson gson = new Gson();

        return gson.fromJson(json, ExtractedText.class);
    }

    /**
     * Method to convert the file accessed by the Uri to an ExtractedText object
     *
     * @param fileUri The Uri to the temp file
     * @param context The context
     * @return ExtractedText object
     * @throws IOException          On IO trouble
     * @throws NullPointerException When the file cannot be found.
     */
    @SuppressWarnings("unused")
    public static ExtractedText getExtractedTextFromFile(@NonNull Uri fileUri,
                                                         @NonNull Context context)
            throws IOException {

        // Open the file
        ParcelFileDescriptor inputPFD = context.getContentResolver().openFileDescriptor(fileUri,
                "r");

        if (inputPFD == null) {
            throw new IllegalArgumentException("The file could not be opened");
        }

        // Read the file
        StringBuilder total = new StringBuilder();
        InputStream fileStream = new FileInputStream(inputPFD.getFileDescriptor());
        try (BufferedReader r = new BufferedReader(new InputStreamReader(fileStream))) {
            for (String line; (line = r.readLine()) != null; ) {
                total.append(line).append('\n');
            }
        }

        // Convert the read file to an ExtractedText object
        return ExtractedText.fromJson(total.toString());
    }

    /**
     * Get the sections of this ExtractedText. Will return an empty list when no Sections are
     * present
     *
     * @return the list of sections
     */
    @NonNull
    public List<Section> getSections() {
        if (mSections != null) {
            return mSections;
        } else {
            return new ArrayList<>();
        }
    }

    /**
     * Adds the section to the list of sections for this extractedText
     *
     * @param section the section to be added
     */
    public void addSection(@NonNull final Section section) {
        if (mSections == null) {
            mSections = new ArrayList<>();
        }
        mSections.add(section);
    }

    /**
     * Adds a new section with only a body
     *
     * @param sectionText the content of the section
     */
    public void addSimpleSection(@NonNull final String sectionText) {
        Section section = new Section(sectionText);
        addSection(section);
    }

    /**
     * @return the (unique) filename given by aurora
     */
    @SuppressWarnings("unused")
    @NonNull
    public String getFilename() {
        if (mFilename == null) {
            return "";
        }
        return mFilename;
    }

    /**
     * Sets the name of the file, this should consist of the filename with a prepended hash to
     * make it unique
     *
     * @param filename the name of the file
     */
    @SuppressWarnings("unused")
    public void setFilename(@NonNull final String filename) {
        mFilename = filename;
    }

    /**
     * @return the display name that should be used in the plugins to display the name of the files.
     */
    @SuppressWarnings({"unused", "WeakerAccess"})
    @NonNull
    public String getFileDisplayName() {
        if (mFilename == null) {
            return "";
        }

        if (mFilename.contains("_")) {
            return mFilename.substring(mFilename.indexOf('_') + 1);
        }
        return mFilename;
    }

    /**
     * @return the title of the text
     */
    @NonNull
    public String getTitle() {
        if (mTitle == null) {
            return "";
        }
        return mTitle;
    }

    /**
     * Sets the title of the text
     *
     * @param title the title to set
     */
    public void setTitle(@NonNull final String title) {
        mTitle = title;
    }

    /**
     * Sets the {@link Annotation} for the title as a Protobuf object generated by
     * {@link ProtobufAnnotationSerializer} that is Serializable
     *
     * @param titleAnnotationProto protobuf object of the Annotation
     */
    public void setTitleAnnotationProto(@Nullable final CoreNLPProtos.Document titleAnnotationProto) {
        mTitleAnnotationProto = titleAnnotationProto;
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
     * @return NonNull List of authors
     */
    @SuppressWarnings({"unused", "WeakerAccess"})
    @NonNull
    public List<String> getAuthors() {
        if (mAuthors == null) {
            return new ArrayList<>();
        }
        return mAuthors;
    }

    /**
     * Sets the List of authors
     *
     * @param authors NonNull list of authors
     */
    @SuppressWarnings({"unused", "WeakerAccess"})
    public void setAuthors(@NonNull final List<String> authors) {
        mAuthors = authors;
    }

    /**
     * Convenience method for retrieving all the images in the {@link ExtractedText}
     * {@link Section}.
     *
     * @return NonNull List of {@link ExtractedImage} objects
     */
    @SuppressWarnings({"unused", "WeakerAccess"})
    @NonNull
    public List<ExtractedImage> getImages() {
        List<ExtractedImage> extractedImages = new ArrayList<>();

        for (Section section : this.getSections()) {
            extractedImages.addAll(section.getExtractedImages());
        }

        return extractedImages;
    }

    @NonNull
    @Override
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
        return res.toString().trim();
    }

    /**
     * Turns the extracted text to a JSON string for easy passing to plugin.
     *
     * @return String (in JSON format)
     */
    @NonNull
    public String toJSON() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }


    @Override
    public boolean equals(Object o) {

        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ExtractedText other = (ExtractedText) o;

        boolean equals;

        equals = this.getFilename().equals(other.getFilename());

        equals &= this.getTitle().equals(other.getTitle());

        if (this.getTitleAnnotation() != null && other.getTitleAnnotation() != null) {
            equals &= this.getTitleAnnotation().equals(other.getTitleAnnotation());
        }
        equals &= this.getAuthors().equals(other.getAuthors());

        equals &= this.getSections().equals(other.getSections());

        return equals;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getFilename(), getTitle(), getTitleAnnotation(), getAuthors(),
                getSections());
    }

}
