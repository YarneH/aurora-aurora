package com.aurora.internalservice.internalprocessor;

import com.aurora.internalservice.InternallyProcessedFile;

import java.util.ArrayList;
import java.util.List;

/**
 * Class to represent extracted text from an internal processor
 */
public class ExtractedText implements InternallyProcessedFile {
    private String mTitle;

    private List<String> mParagraphs;

    public ExtractedText(String title, List<String> paragraphs) {
        mTitle = title;
        mParagraphs = paragraphs;
    }

    public ExtractedText() {
        mTitle = null;
        mParagraphs = new ArrayList<>();
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        this.mTitle = title;
    }

    public List<String> getParagraphs() {
        return mParagraphs;
    }

    public void addParagraph(String paragraph) {
        this.mParagraphs.add(paragraph);
    }
}
