package com.aurora.auroralib;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Base64;
import java.util.Collections;

public class SectionUnitTest {

    private static ExtractedImage extractedImage1;
    private static ExtractedImage extractedImage2;

    @BeforeClass
    public static void setup() throws IOException {
        File image1 =  new File("src/test/java/com/aurora/auroralib/testimage1.png");
        String encodedImage1 = Base64.getEncoder().encodeToString(Files.readAllBytes(image1.toPath()));
        extractedImage1 = new ExtractedImage(encodedImage1);

        File image2 =  new File("src/test/java/com/aurora/auroralib/testimage2.png");
        String encodedImage2 =
                Base64.getEncoder().encodeToString(Files.readAllBytes(image2.toPath()));
        extractedImage2 = new ExtractedImage(encodedImage2);
    }

    @Test
    public void Section_copyConstructor_shouldReturnCopy() {
        Section section = new Section("sectionContent");
        section.setTitle("sectionTitle");
        section.addExtractedImage(extractedImage1);
        section.addExtractedImages(Collections.singletonList(extractedImage2));

        Section copy = new Section(section);

        Assert.assertEquals(section, copy);
    }

    @Test
    public void Section_getters_shouldNotReturnNull() {
        Section section = new Section();

        Assert.assertNotNull(section.getBody());
        Assert.assertNotNull(section.getTitle());
        Assert.assertNotNull(section.getExtractedImages());
    }

    @Test
    public void Section_concatBody_shouldConcatBody() {
        Section section = new Section();
        final String part1 = "part1";
        final String part2 = "part2";

        section.concatBody(part1);
        section.concatBody(part2);

        Assert.assertEquals(section.getBody(), part1 + part2);

    }
}
