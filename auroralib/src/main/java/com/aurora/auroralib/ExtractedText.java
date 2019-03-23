package com.aurora.auroralib;

import java.io.Serializable;
import java.util.List;

/**
 * Class to represent extracted text from an internal processor
 */
public class ExtractedText implements InternallyProcessedFile, Serializable {
    private String mTitle;

    private List<String> mParagraphs;

    public ExtractedText(String title, List<String> paragraphs) {
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
