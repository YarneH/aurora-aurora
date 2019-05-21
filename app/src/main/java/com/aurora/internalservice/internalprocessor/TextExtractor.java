package com.aurora.internalservice.internalprocessor;


import com.aurora.auroralib.ExtractedText;

import java.io.InputStream;

/**
 * Interface for different extractors, extracting text from different file formats
 */
public interface TextExtractor {
    /**
     * @param file          InputStream to the file
     * @param fileUri       the uri of the file to be processed
     * @param fileRef       the name of the file
     * @param extractImages True if images need to be extracted, False otherwise
     * @return the extracted text, split in parts
     * @throws DocumentNotSupportedException if the document cannot be processed
     */
    ExtractedText extract(InputStream file, String fileUri, String fileRef, boolean extractImages)
            throws DocumentNotSupportedException;
}
