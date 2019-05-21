package com.aurora.internalservice.internalprocessor;

import com.aurora.auroralib.ExtractedText;

import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import static org.junit.Assert.*;

public class TextExtractorDOCXUnitTest {

    private ExtractedText mExtractedText;

    private final static String RES_PATH = "src/test/res/";
    // Extracts the text before the test so the tests can reuse the ExtractedText
    @Before
    public void initialize() throws FileNotFoundException {
        // Initialize the extractor
        TextExtractorDOCX textExtractorDOCX = new TextExtractorDOCX();
        String fileUri = "dummyUri";
        String mfileRef = RES_PATH + "Pasta.docx";
        File file = new File(mfileRef);
        InputStream inputStream = new FileInputStream(file);
        // Extract the text
        mExtractedText = textExtractorDOCX.extract(inputStream, fileUri, mfileRef, false);
    }

    @Test
    public void extract_shouldReturnExtractedText() {
        assertNotNull("DOCX text extraction: Failed to extract", mExtractedText);
    }

    // Checks if the title is extracted correctly
    @Test
    public void extract_shouldExtractTitleCorrectly() {

            assertEquals("DOCX text extraction:Title is not the correctly extracted",
                    "Pasta puttanesca",
                    mExtractedText.getTitle());
    }

    // Checks if the title of a section is extracted correctly
    @Test
    public void extract_shouldExtractSectionTitleCorrectly() {
        assertEquals("DOCX text extraction: SectionTitle is not extracted correctly",
                "Yield",
                mExtractedText.getSections().get(0).getTitle());
    }

    // Checks if the body of a section is extracted correctly
    @Test
    public void extract_shouldExtractSectionBodyCorrectly() {
        assertEquals("DOCX text extraction: Section is not extracted correctly",
                "4 servings\n" +
                        "Active Time\n" +
                        "30 minutes\n" +
                        "Total Time\n" +
                        "35 minutes\n", mExtractedText.getSections().get(0).getBody());
    }



}