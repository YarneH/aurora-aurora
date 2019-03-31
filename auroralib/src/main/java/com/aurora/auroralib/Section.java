package com.aurora.auroralib;

import java.util.List;

public class Section {

    private String title;
    private String body;
    private List<String> images;

    /**
     * Constructor for creating a section without images or title
     * @param body the content of the section
     */
    public Section(String body) {
        this.body = body;
    }

    /**
     * Constructor for creating a section with a title, content and images
     * @param title the title of the section
     * @param body the content of the section
     * @param images the images in the section
     */
    public Section(String title, String body, List<String> images) {
        this.title = title;
        this.body = body;
        this.images = images;
    }

    @Override
    public String toString() {
        return title + "\n" + body + "\n";
    }
}
