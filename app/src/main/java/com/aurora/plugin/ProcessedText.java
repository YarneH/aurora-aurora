package com.aurora.plugin;

import java.util.List;

/**
 * Class to represent text processed by the plugin
 */
public abstract class ProcessedText {
    /**
     * The title of the text
     */
    private String mTitle;

    /**
     * Different 'paragraphs' or subdivisions in text
     */
    private List<String> mParagraphs; // TODO: Data type may change

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
