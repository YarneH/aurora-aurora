package com.aurora.auroralib;

import com.google.gson.Gson;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ExtractedTextUnitTest {

    private static final String FILENAME = "13456_dummyFileName";

    @Test
    public void ExtractedText_fromJson_shouldCreateExtractedTextObjectFromJsonString() {
        // Create Extracted text object and then manually create jsonString
        List<String> sections = Arrays.asList("Hello", "there", "General", "Kenobi");
        ExtractedText extractedText = new ExtractedText(FILENAME, sections);

        Gson gson = new Gson();
        String jsonString = gson.toJson(extractedText, ExtractedText.class);

        // Call method under test
        ExtractedText reconstructed = ExtractedText.fromJson(jsonString);

        // Work with string comparison because somehow the objects were different (the diff showed one white
        // space at the end of the object (not inside but outside the object)
        Assert.assertEquals(extractedText.toString(), reconstructed.toString());
    }

    @Test
    public void ExtractedText_getters_shouldRetrieveElements() {
        ExtractedText extractedText = new ExtractedText(FILENAME);

        final String title = "UselessTitle";
        final List<String> authors = Collections.singletonList("John");
        final Section section1 = new Section("section1");
        final Section section2 = new Section("section2");

        List<Section> sections = new ArrayList<>();
        sections.add(section1);
        sections.add(section2);

        extractedText.setTitle(title);
        extractedText.setAuthors(authors);
        extractedText.addSection(section1);
        extractedText.addSection(section2);

        Assert.assertEquals(extractedText.getFilename(), FILENAME);
        Assert.assertEquals(extractedText.getFileDisplayName(),
                FILENAME.substring(FILENAME.indexOf('_') + 1));
        Assert.assertEquals(extractedText.getTitle(), title);
        Assert.assertEquals(extractedText.getAuthors(), authors);
        Assert.assertEquals(extractedText.getSections(), sections);
    }

    @Test
    public void ExtractedText_getters_shouldNotReturnNull() {
        ExtractedText extractedText = new ExtractedText(FILENAME);

        Assert.assertNotNull(extractedText.getFilename());
        Assert.assertNotNull(extractedText.getFileDisplayName());
        Assert.assertNotNull(extractedText.getTitle());
        Assert.assertNotNull(extractedText.getSections());
        Assert.assertNotNull(extractedText.getAuthors());
        Assert.assertNotNull(extractedText.getImages());
    }
}
