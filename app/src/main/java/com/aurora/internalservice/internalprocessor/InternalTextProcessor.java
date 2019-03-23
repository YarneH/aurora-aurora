package com.aurora.internalservice.internalprocessor;

import android.util.Log;

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
     *
     * @param fileRef a reference to where the file can be found
     * @return The extracted content from the file
     */
    public ExtractedText processFile(InputStream file, String fileRef) throws FileTypeNotSupportedException {
        Log.d("InternalTextProcessing", "Not implemented yet!");

        ExtractedText extractedText;
        String fileType = getMimeType(fileRef);
        TextExtractor extractor = fileFormatExtractorMap.get(fileType);
        if (extractor != null) {
            // TODO make this generic
            extractedText = extractor.extract(fileRef);
            if(extractor instanceof TextExtractorDOCX) {
                ((TextExtractorDOCX) extractor).extract(file,fileRef);
            }
        } else {
            Log.d("InternalTextProcessor", "File type not supported");
            throw new FileTypeNotSupportedException("");
        }
        return extractedText;
    }

    /**
     * @param fileRef the reference of the to be processed file
     * @return the extension of the file
     */
    // TODO: implement for real
    private static String getMimeType(String fileRef) {
        String[] splitted = fileRef.split("\\.");
        return splitted[splitted.length - 1];
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
