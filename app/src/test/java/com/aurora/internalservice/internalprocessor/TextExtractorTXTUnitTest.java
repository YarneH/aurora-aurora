package com.aurora.internalservice.internalprocessor;

import com.aurora.auroralib.ExtractedText;

import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class TextExtractorTXTUnitTest {
    private final static String RES_PATH = "src/test/res/";
    private ExtractedText mExtractedText;

    @Before
    public void initialize() throws FileNotFoundException {
        TextExtractorTXT textExtractorTXT = new TextExtractorTXT();
        String fileUri = "dummyUri";
        String fileRef = RES_PATH + "Pasta.txt";
        File file = new File(fileRef);
        InputStream inputStream = new FileInputStream(file);
        mExtractedText = textExtractorTXT.extract(inputStream, fileUri, fileRef, false);
    }


    @Test
    public void extract_shouldReturnExtractedText() {
        assertNotNull("TXT text extraction: Failed to extract", mExtractedText);
    }


    @Test
    public void extract_shouldExtractTitleCorrectly() {
        assertEquals("TXT text extraction: Titles are not the same",
                "Pasta puttanesca",
                mExtractedText.getTitle());
    }

    @Test
    public void extract_shouldExtractSectionZeroCorrectly() {
        assertEquals("TXT text extraction: Section is not extracted correctly",
                "Yield\n    4 servings\n" +
                        "Active Time\n    30 minutes\n" +
                        "Total Time\n    35 minutes",
                mExtractedText.getSections().get(0).getBody());
    }

    @Test
    public void extract_shouldExtractSectionOneCorrectly() {
        assertEquals("TXT text extraction: Section is not extracted correctly",
                "Ingredients",
                mExtractedText.getSections().get(1).getBody());
    }



}