package com.aurora.internalservice.internalprocessor;

import com.aurora.auroralib.ExtractedText;

import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import static org.junit.Assert.*;

public class TextExtractorTXTTest {
    private static TextExtractorTXT mTextExtractorTXT = new TextExtractorTXT();

    @Test
    public void extract_shouldExtractTextCorrectly() {
        String fileRef = "src/test/res/Pasta.txt";
        File file = new File(fileRef);
        try {
            InputStream inputStream = new FileInputStream(file);
            ExtractedText extractedText = mTextExtractorTXT.extract(inputStream, fileRef);
            assertEquals("TXT text extraction:Titles are not the same",
                    extractedText.getTitle(), "Pasta puttanesca");
            assertEquals("TXT text extraction: Section is not extracted correctly",
                    "Yield\n    4 servings\n" +
                            "Active Time\n    30 minutes\n" +
                            "Total Time\n    35 minutes",
                    extractedText.getSections().get(0).getBody());
            assertEquals("TXT text extraction: Section is not extracted correctly",
                    "Ingredients",
                    extractedText.getSections().get(1).getBody());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}