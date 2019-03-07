package com.aurora.internalservice.internalprocessor;

import com.aurora.internalservice.InternallyProcessedFile;

import java.util.List;

/**
 * Class to represent extracted text from an internal processor
 */
public class ExtractedText implements InternallyProcessedFile {
    private String mTitle;

    private List<String> mParagraphs;

    ExtractedText(String title, List<String> paragraphs) {
        mTitle = title;
        mParagraphs = paragraphs;
    }

    public String getTitle() {
        return mTitle;
    }

    public List<String> getParagraphs() {
        return mParagraphs;
    }
}
