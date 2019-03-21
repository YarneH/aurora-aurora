package com.aurora.internalservice.internalprocessor;

import com.aurora.auroralib.ExtractedText;

public class TextExtractorTXT implements TextExtractor {

    /**
     * Extracts the text from a .txt file.
     * @param fileRef a reference to where the file can be found
     * @return an ExtractedText object without title and one line per paragraph.
     */
    @Override
    public ExtractedText extract(String fileRef) {
        return new ExtractedText(fileRef, null);
    }
}
