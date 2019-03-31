package com.aurora.internalservice.internalprocessor;

import java.io.InputStream;

import com.aurora.auroralib.ExtractedText;

public class TextExtractorPDF implements TextExtractor {
    /**
     * TODO: This method will extract the text using iText
     * @param fileRef a reference to where the file can be found
     * @return the extracted text from the file on fileRef
     */
    @Override
    public ExtractedText extract(InputStream file, String fileRef) {
        return new ExtractedText(fileRef, null);
    }
}
