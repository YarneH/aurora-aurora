package com.aurora.auroralib;

import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Base64;

public class ExtractedImageUnitTest {

    @Test
    public void ExtractedImage_getters_shouldNotReturnNull() throws IOException {
        File image1 =  new File("src\\test\\java\\com\\aurora\\auroralib\\testimage1.png");
        String encodedImage1 = Base64.getEncoder().encodeToString(Files.readAllBytes(image1.toPath()));
        ExtractedImage image = new ExtractedImage(encodedImage1);

        Assert.assertNotNull(image.getBase64EncodedImage());
        Assert.assertNotNull(image.getCaption());
    }
}
