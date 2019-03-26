package com.aurora.internalservice.internalprocessor;

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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }
}