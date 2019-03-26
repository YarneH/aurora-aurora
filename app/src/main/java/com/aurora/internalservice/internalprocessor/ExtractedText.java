package com.aurora.internalservice.internalprocessor;

import com.aurora.internalservice.InternallyProcessedFile;

import java.util.Date;
import java.util.List;

/**
 * Class to represent extracted text from an internal processor
 */
public class ExtractedText implements InternallyProcessedFile {
    private String filename;
    private Date dateLastEdit;
    private String title;
    private List<String> authors;
    private List<Section> sections;


    public ExtractedText() {
    }

    public ExtractedText(String filename, Date dateLastEdit, String title, List<String> authors, List<Section> sections) {
        this.filename = filename;
        this.dateLastEdit = dateLastEdit;
        this.title = title;
        this.authors = authors;
        this.sections = sections;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public Date getDateLastEdit() {
        return dateLastEdit;
    }

    public void setDateLastEdit(Date dateLastEdit) {
        this.dateLastEdit = dateLastEdit;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<String> getAuthors() {
        return authors;
    }

    public void setAuthors(List<String> authors) {
        this.authors = authors;
    }

    public void setSections(List<Section> sections) {
        this.sections = sections;
    }

    public String getTitle() {
        return title;
    }

    public List<Section> getSections() {
        return sections;
    }
}
