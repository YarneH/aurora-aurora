package com.aurora.auroralib;

import com.google.gson.Gson;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.List;

public class ExtractedTextUnitTest {

    private static final String FILENAME = "13456_dummyFileName";

    private static final String title = "UselessTitle";
    private static final List<String> authors = Collections.singletonList("John");
    private static final Section section1 = new Section("section1");
    private static final Section section2 = new Section("section2");
    private static final List<ExtractedImage> images = new ArrayList<>();

    @BeforeClass
    public static void setup() throws IOException {
        File image1 =  new File("src\\test\\java\\com\\aurora\\auroralib\\testimage1.png");
        String encodedImage1 = Base64.getEncoder().encodeToString(Files.readAllBytes(image1.toPath()));
        ExtractedImage extractedImage1 = new ExtractedImage(encodedImage1);
        section1.addExtractedImage(extractedImage1);

        File image2 =  new File("src\\test\\java\\com\\aurora\\auroralib\\testimage2.png");
        String encodedImage2 =
                Base64.getEncoder().encodeToString(Files.readAllBytes(image2.toPath()));
        ExtractedImage extractedImage2 = new ExtractedImage(encodedImage1);
        ExtractedImage extractedImage3 = new ExtractedImage(encodedImage2);

        section1.addExtractedImage(extractedImage2);
        section2.addExtractedImage(extractedImage3);

        images.add(extractedImage1);
        images.add(extractedImage2);
        images.add(extractedImage3);
    }

    @Test
    public void ExtractedText_fromJson_shouldCreateExtractedTextObjectFromJsonString() {
        // Create Extracted text object and then manually create jsonString
        List<String> sections = Arrays.asList("Hello", "there", "General", "Kenobi");
        ExtractedText extractedText = new ExtractedText(FILENAME, sections);

        Gson gson = new Gson();
        String jsonString = gson.toJson(extractedText, ExtractedText.class);

        // Call method under test
        ExtractedText reconstructed = ExtractedText.fromJson(jsonString);

        // Work with string comparison because somehow the objects were different (the diff
        // showed one white
        // space at the end of the object (not inside but outside the object)
        Assert.assertEquals(extractedText.toString(), reconstructed.toString());
    }

    @Test
    public void ExtractedText_getters_shouldRetrieveElements() {
        ExtractedText extractedText = new ExtractedText(FILENAME);

        List<Section> sections = new ArrayList<>();
        sections.add(section1);
        sections.add(section2);

        extractedText.setTitle(title);
        extractedText.setAuthors(authors);
        extractedText.addSection(section1);
        extractedText.addSection(section2);
        extractedText.addSimpleSection("section3");
        sections.add(new Section("section3"));

        Assert.assertEquals(extractedText.getFilename(), FILENAME);
        Assert.assertEquals(extractedText.getFileDisplayName(),
                FILENAME.substring(FILENAME.indexOf('_') + 1));
        Assert.assertEquals(extractedText.getTitle(), title);
        Assert.assertEquals(extractedText.getAuthors(), authors);
        Assert.assertEquals(extractedText.getSections(), sections);

        Assert.assertEquals(extractedText.getImages(), images);
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

    @Test
    public void ExctractedText_fromJson_toJsonFromJsonShouldBeIdemPotent(){

        ExtractedText extractedText = new ExtractedText(FILENAME);

        extractedText.setTitle(title);
        extractedText.setAuthors(authors);
        extractedText.addSection(section1);
        extractedText.addSection(section2);

        Assert.assertEquals(extractedText, ExtractedText.fromJson(extractedText.toJSON()));
    }

}
