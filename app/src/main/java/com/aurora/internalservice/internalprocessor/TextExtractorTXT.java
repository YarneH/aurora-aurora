package com.aurora.internalservice.internalprocessor;

import java.io.InputStream;

public class TextExtractorTXT implements TextExtractor {

    /**
     * Extracts the text from a .txt file.
     * @param fileRef a reference to where the file can be found
     * @return an ExtractedText object without title and one line per paragraph.
     */
    @Override
    public ExtractedText extract(InputStream file, String fileRef) {
        return new ExtractedText(fileRef, null);
    }
}
