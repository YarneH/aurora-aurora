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

public class TextExtractorPDFUnitTest {

    private ExtractedText mExtractedText;

    private final static String RES_PATH = "src/test/res/";

    // Extracts the text before the test so the tests can reuse the ExtractedText
    @Ignore("PDF extraction is not yet implemented")
    @Before
    public void initialize() throws FileNotFoundException {
        // Initialize the extractor
        TextExtractorPDF textExtractorPDF = new TextExtractorPDF();
        String mfileRef = RES_PATH + "/Pasta.pdf";
        File file = new File(mfileRef);
        InputStream inputStream = new FileInputStream(file);
        // Extract the text
        try {
            mExtractedText = textExtractorPDF.extract(inputStream, mfileRef, false);
        } catch (DocumentNotSupportedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void extract_shouldReturnExtractedText() {
        assertNotNull("PDF text extraction: Failed to extract", mExtractedText);
    }

    @Test
    public void extract_shouldExtractTitleCorrectly() {

        assertEquals("PDF text extraction:Title is not the correctly extracted",
                "Pasta puttanesca",
                mExtractedText.getTitle());
    }

    /**
     *  Checks if the title of a section is extracted correctly
     */

    @Test
    public void extract_shouldExtractSectionTitleCorrectly() {
        assertEquals("PDF text extraction: SectionTitle is not extracted correctly",
                "General Properties ",
                mExtractedText.getSections().get(0).getTitle());
    }

    // Checks if the body of a section is extracted correctly
    @Test
    public void extract_shouldExtractSectionBodyCorrectly() {
        assertEquals("PDF text extraction: Section is not extracted correctly",
                "4 servings ",
                mExtractedText.getSections().get(0).getBody().split("\n")[1]);
    }


}