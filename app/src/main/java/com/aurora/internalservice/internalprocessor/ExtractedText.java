package com.aurora.internalservice.internalprocessor;

import com.aurora.internalservice.InternallyProcessedFile;

import java.util.ArrayList;
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

    /**
     * This constructor will create an empty extracted text
     *
     * @param filename     the name of the file
     * @param dateLastEdit the moment the file was last edited
     */
    public ExtractedText(String filename, Date dateLastEdit) {
        this.filename = filename;
        this.dateLastEdit = dateLastEdit;
    }

    /**
     * Represent an extracted text with all arguments
     *
     * @param filename     the name of the file
     * @param dateLastEdit the moment the file was last edited
     * @param title        the title of the file
     * @param authors      the authors of the file
     * @param sections     the sections in the file
     */
    public ExtractedText(String filename, Date dateLastEdit, String title, List<String> authors, List<Section> sections) {
        this(filename, dateLastEdit);
        this.title = title;
        if (authors != null) {
            this.authors = authors;
        }
        if (sections != null) {
            this.sections = sections;
        }

    }

    /**
     * get the filename
     *
     * @return filename
     */
    public String getFilename() {
        return filename;
    }

    /**
     * set the filename
     */
    public void setFilename(String filename) {
        this.filename = filename;
    }

    /**
     * get the date the file was last edited
     *
     * @return date last edit
     */
    public Date getDateLastEdit() {
        return dateLastEdit;
    }

    /**
     * set the date the file was last edited
     */
    public void setDateLastEdit(Date dateLastEdit) {
        this.dateLastEdit = dateLastEdit;
    }

    /**
     * adds a section to the list of sections
     *
     * @param section the section to add
     */
    public void addSection(Section section) {
        sections.add(section);
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setAuthors(List<String> authors) {
        this.authors = authors;
    }

    public List<String> getAuthors() {
        return authors;
    }

    public String getTitle() {
        return title;
    }
}
