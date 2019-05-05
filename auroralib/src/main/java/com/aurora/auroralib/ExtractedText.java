package com.aurora.auroralib;

import android.content.Context;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.annotations.JsonAdapter;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.CoreNLPProtos;
import edu.stanford.nlp.pipeline.ProtobufAnnotationSerializer;


/**
 * Class to represent extracted text from an internal processor
 */
public class ExtractedText implements InternallyProcessedFile, Serializable {

    /**
     * The filename, often the path to the file
     */
    private String mFilename;
    /**
     * The Date of the last edit
     */
    private Date mDateLastEdit;

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
     * Get the sections of this ExtractedText. Will return an empty list when no Sections are
     * present
     *
     * @return the list of sections
     */
    public List<Section> getSections() {
        if (mSections != null) {
            return this.mSections;
        } else {
            return new ArrayList<>();
        }
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
     * Adds a new section with only a body
     *
     * @param sectionText the content of the section
     */
    public void addSimpleSection(String sectionText) {
        Section section = new Section();
        section.setBody(sectionText);
        addSection(section);
    }

    @SuppressWarnings("unused")
    public String getFilename() {
        return mFilename;
    }

    public void setFilename(String mFilename) {
        this.mFilename = mFilename;
    }

    @SuppressWarnings("unused")
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

    public void setTitleAnnotationProto(CoreNLPProtos.Document titleAnnotationProto) {
        this.mTitleAnnotationProto = titleAnnotationProto;
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

    @SuppressWarnings("unused")
    public List<String> getAuthors() {
        if (mAuthors != null) {
            return  mAuthors;
        } else {
            return new ArrayList<>();
        }
    }

    public void setAuthors(List<String> authors) {
        this.mAuthors = authors;
    }

    /**
     * Convenience method for getting all the images
     *
     * @return List of {@link Image} objects
     */
    @SuppressWarnings("unused")
    public List<Image> getImages() {
        List<Image> extractedImages = new ArrayList<>();

        for (Section section: this.getSections()) {
            extractedImages.addAll(section.getImageObjects());
        }

        return extractedImages;
    }

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
    @SuppressWarnings("unused")
    public static ExtractedText fromJson(String json) {
        Gson gson = new Gson();

        return gson.fromJson(json, ExtractedText.class);
    }

    /**
     * Method to convert the file accessed by the Uri to an ExtractedText object
     *
     * @param fileUri   The Uri to the temp file
     * @param context   The conext
     * @return  ExtractedText object
     * @throws IOException          On IO trouble
     * @throws NullPointerException When the file cannot be found.
     */
    @SuppressWarnings("unused")
    public static ExtractedText getExtractedTextFromFile(@NonNull Uri fileUri, @NonNull Context context)
            throws IOException {

        // Open the file
        ParcelFileDescriptor inputPFD = context.getContentResolver().openFileDescriptor(fileUri, "r");

        if(inputPFD == null) {
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
}
