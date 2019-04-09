package com.aurora.internalservice.internalprocessor;

import com.aurora.auroralib.ExtractedText;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import static org.junit.Assert.*;

public class TextExtractorDOCXTest {

    private ExtractedText mExtractedText;

    @Before
    public void initialize() throws FileNotFoundException {
        /* Set system properties for DOCX */
        System.setProperty("org.apache.poi.javax.xml.stream.XMLInputFactory",
                "com.fasterxml.aalto.stax.InputFactoryImpl");
        System.setProperty("org.apache.poi.javax.xml.stream.XMLOutputFactory",
                "com.fasterxml.aalto.stax.OutputFactoryImpl");
        System.setProperty("org.apache.poi.javax.xml.stream.XMLEventFactory",
                "com.fasterxml.aalto.stax.EventFactoryImpl");

        TextExtractorDOCX textExtractorDOCX = new TextExtractorDOCX();
        String mfileRef = "src/test/res/Pasta.docx";
        File file = new File(mfileRef);
        InputStream inputStream = new FileInputStream(file);
        mExtractedText = textExtractorDOCX.extract(inputStream, mfileRef);
    }

    @Test
    public void extract_shouldReturnExtractedText() {
        assertNotNull("DOCX text extraction: Failed to extract", mExtractedText);
    }


    @Ignore("The implementation of DOCX extraction is not yet on this level")
    @Test
    public void extract_shouldExtractTextCorrectly() {

            assertEquals("DOCX text extraction:Titles are not the same",
                    "Pasta puttanesca",
                    mExtractedText.getTitle());
            assertEquals("DOCX text extraction: Section is not extracted correctly",
                    "Yield",
                    mExtractedText.getSections().get(0).getTitle());
            assertEquals("DOCX text extraction: Section is not extracted correctly",
                    "4 servings",
                    mExtractedText.getSections().get(0).getBody());
            assertEquals("DOCX text extraction: Section is not extracted correctly",
                    "Preparation",
                    mExtractedText.getSections().get(4).getTitle());

    }
}