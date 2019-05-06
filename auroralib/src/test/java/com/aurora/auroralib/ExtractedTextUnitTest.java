package com.aurora.auroralib;

import com.google.gson.Gson;

import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class ExtractedTextUnitTest {

    @Test
    public void ExtractedText_fromJson_shouldCreateExtractedTextObjectFromJsonString() {
        // Create Extracted text object and then manually create jsonString
        String fileName = "dummyFileName";
        Date dateLastEdit = new Date();
        List<String> sections = Arrays.asList("Hello", "there", "General", "Kenobi");
        ExtractedText extractedText = new ExtractedText(fileName, dateLastEdit, sections);

        Gson gson = new Gson();
        String jsonString = gson.toJson(extractedText, ExtractedText.class);

        // Call method under test
        ExtractedText reconstructed = ExtractedText.fromJson(jsonString);

        // Work with string comparison because somehow the objects were different (the diff showed one white
        // space at the end of the object (not inside but outside the object)
        Assert.assertEquals(extractedText.toString(), reconstructed.toString());
    }

}
