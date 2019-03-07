package com.aurora.internalservice.internalprocessor;

import android.util.Log;

import com.aurora.internalservice.InternalService;

import java.util.HashMap;

/**
 * Class that takes care of internal processing of file (such as text extraction)
 */
public class InternalTextProcessing implements InternalService {

    private HashMap<String, TextExtractor> fileFormatExtractorMap = createFileToExtractorMap();

    // TODO: return type may change as well as fileRef type

    /**
     * extracts text from a file using the right Extractor
     *
     * @param fileRef a reference to where the file can be found
     */
    @Override
    public void process(String fileRef) {
        Log.d("InternalTextProcessing", "Not implemented yet!");

        ExtractedText extractedText;
        if (fileFormatExtractorMap.containsKey(getMimeType(fileRef))) {
            extractedText = fileFormatExtractorMap.get(getMimeType(fileRef)).extract(fileRef);
        } else {
            Log.d("InternalTextProcessing", "File type not supported");
        }

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
    private HashMap<String, TextExtractor> createFileToExtractorMap() {
        HashMap<String, TextExtractor> map = new HashMap<>();
        map.put("txt", new TextExtractorTXT());
        map.put("pdf", new TextExtractorPDF());
        return map;
    }
}
