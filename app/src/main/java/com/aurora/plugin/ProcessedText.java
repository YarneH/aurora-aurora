package com.aurora.plugin;

import java.util.List;

/**
 * Class to represent text processed by the plugin
 */
public class ProcessedText {
    /**
     * The title of the text
     */
    private String mTitle;

    // TODO: Data type may change
    /**
     * Different 'paragraphs' or subdivisions in text
     */
    private List<String> mParagraphs;

    public ProcessedText(String title, List<String> paragraphs) {
        this.mTitle = title;
        mParagraphs = paragraphs;
    }

    public String getTitle() {
        return mTitle;
    }

    public List<String> getParagraphs() {
        return mParagraphs;
    }
}
