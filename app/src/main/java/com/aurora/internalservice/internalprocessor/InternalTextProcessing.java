package com.aurora.internalservice.internalprocessor;

import android.util.Log;

import com.aurora.internalservice.InternalService;

import java.io.IOException;
import java.util.HashMap;

/**
 * Class that takes care of internal processing of file (such as text extraction)
 */
public class InternalTextProcessing implements InternalService {

    private HashMap<String, TextExtractor> fileformat_ExtractorMap = create_File_to_Extractor_map();

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
        if (fileformat_ExtractorMap.containsKey(getMimeType(fileRef))){
            extractedText = fileformat_ExtractorMap.get(getMimeType(fileRef)).extract(fileRef);
        } else {
            Log.d("InternalTextProcessing", "File type not supported");
        }

    }

    /**
     *
     * @param fileRef the reference of the to be processed file
     * @return the extension of the file
     */
    // TODO: implement
    private String getMimeType(String fileRef){
        return null;
    }

    /**
     *
     * @return the mapping of fileTypes to the related extractor
     */
    private HashMap<String, TextExtractor> create_File_to_Extractor_map() {
        HashMap<String,TextExtractor> map = new HashMap<>();
        map.put("txt", new TextExtractorTXT());
        map.put("pdf", new TextExtractorPDF());
        return map;
    }
}
