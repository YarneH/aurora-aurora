package com.aurora.internalservice.internalprocessor.pdfparsing;

import android.util.Base64;
import android.util.Log;

import com.itextpdf.text.pdf.parser.ImageRenderInfo;
import com.itextpdf.text.pdf.parser.TextExtractionStrategy;
import com.itextpdf.text.pdf.parser.TextRenderInfo;

import java.io.IOException;

/**
 * The ImageExtractionStrategy extracts an image from a pdf
 */
public class ImageExtractionStrategy implements TextExtractionStrategy {
    private String mImage;

    ImageExtractionStrategy() {
        this.mImage = "";
    }

    @Override
    public String getResultantText() {
        return mImage;
    }

    @Override
    public void beginTextBlock() {
        // Not needed for images
    }

    @Override
    public void renderText(TextRenderInfo renderInfo) {
        // Not needed for images
    }

    @Override
    public void endTextBlock() {
        // Not needed for images
    }

    @Override
    public void renderImage(ImageRenderInfo renderInfo) {
        try {
            mImage = android.util.Base64.encodeToString(renderInfo.getImage().getImageAsBytes(), Base64.DEFAULT);
        } catch (IOException e) {
            Log.e("PDF image extraction", e.getLocalizedMessage());
        }

    }
}
