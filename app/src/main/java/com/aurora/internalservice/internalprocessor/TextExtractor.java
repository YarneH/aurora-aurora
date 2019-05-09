package com.aurora.internalservice.internalprocessor;


import java.io.InputStream;

import com.aurora.auroralib.ExtractedText;

/**
 * Interface for different extractors, extracting text from different file formats
 */
public interface TextExtractor {
    /**
     * @param file          InputStream to the file
     * @param fileRef       a reference to where the file can be found
     * @param extractImages True if images need to be extracted, False otherwise
     * @return the extracted text, split in parts
     */
    ExtractedText extract(InputStream file, String fileRef, boolean extractImages) throws DocumentNotSupportedException;
}
