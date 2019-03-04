package com.aurora.internalservice.internalprocessor;

public class TextExtractorPDF implements TextExtractor {
    @Override
    public ExtractedText extract(String fileRef) {
        return new ExtractedText("",null);
    }
}
