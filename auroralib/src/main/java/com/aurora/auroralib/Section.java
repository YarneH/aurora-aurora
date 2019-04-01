package com.aurora.auroralib;

import java.util.List;

public class Section {
    /**
     * The title of a Section
     */
    private String mTitle;
    /**
     * The content/body of a Section (the text)
     */
    private String mBody;
    /**
     * The images in a section, as a Base64 String
     */
    private List<String> mImages;

    /**
     * Constructor for creating a section without images or title
     * @param body the content of the section
     */
    public Section(String body) {
        this.mBody = body;
    }

    /**
     * Constructor for creating a section with a title, content and images
     * @param title the title of the section
     * @param body the content of the section
     * @param images the images in the section
     */
    public Section(String title, String body, List<String> images) {
        this.mTitle = title;
        this.mBody = body;
        this.mImages = images;
    }

    @Override
    public String toString() {
        if (mTitle != null && mBody != null){
            return mTitle + "\n" + mBody + "\n";
        } else if (mBody != null){
            return mBody + "\n";
        } else return "Empty paragraph.\n";
    }

    public String getTitle() {
        return mTitle;
    }

    public String getBody() {
        return mBody;
    }

    public List<String> getImages() {
        return mImages;
    }
}
