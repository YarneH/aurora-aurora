package com.aurora.auroralib;

import java.util.List;

public class Section {

    private String title;
    private String body;
    private List<String> images;

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
