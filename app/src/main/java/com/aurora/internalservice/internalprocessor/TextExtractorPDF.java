package com.aurora.internalservice.internalprocessor;

public class TextExtractorPDF implements TextExtractor {
    /**
     * TODO: This method will extract the text using iText
     * @param fileRef a reference to where the file can be found
     * @return the extracted text from the file on fileRef
     */
    @Override
    public ExtractedText extract(String fileRef) {
        return new ExtractedText("",null);
    }
}
