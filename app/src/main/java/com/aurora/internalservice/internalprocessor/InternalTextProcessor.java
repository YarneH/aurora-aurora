package com.aurora.internalservice.internalprocessor;

import android.util.Log;

import com.aurora.auroralib.ExtractedText;
import com.aurora.internalservice.InternalService;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Class that takes care of internal processing of file (such as text extraction)
 */
public class InternalTextProcessor implements InternalService {

    private Map<String, TextExtractor> fileFormatExtractorMap = createFileToExtractorMap();


    /**
     * extracts text from a file using the right Extractor
     * @param file the stream containing the file
     * @param fileRef a reference to where the file can be found
     * @param type the mimetype of the file
     * @return The extracted content from the file
     */
    public ExtractedText processFile(InputStream file, String fileRef, String type)
            throws FileTypeNotSupportedException {
        ExtractedText extractedText;
        TextExtractor extractor = fileFormatExtractorMap.get(type);
        if (extractor != null) {
            extractedText = extractor.extract(file, fileRef);
        } else {
            Log.d("InternalTextProcessor", "File type not supported");
            throw new FileTypeNotSupportedException("");
        }
        return extractedText;
    }

    /**
     * @return the mapping of fileTypes to the related extractor
     */
    private Map<String, TextExtractor> createFileToExtractorMap() {
        Map<String, TextExtractor> map = new HashMap<>();
        map.put("txt", new TextExtractorTXT());
        map.put("pdf", new TextExtractorPDF());
        map.put("docx", new TextExtractorDOCX());
        return map;
    }
}
