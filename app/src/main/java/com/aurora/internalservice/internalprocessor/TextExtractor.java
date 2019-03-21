package com.aurora.internalservice.internalprocessor;


import com.aurora.auroralib.ExtractedText;

/**
 * Interface for different extractors, extracting text from different file formats
 */
public interface TextExtractor {
    /**
     *
     * @param fileRef a reference to where the file can be found
     * @return the extracted text, split in parts
     */
    ExtractedText extract(String fileRef);
}
