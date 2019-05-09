package com.aurora.internalservice.internalprocessor;

import android.util.Log;

import com.aurora.auroralib.ExtractedText;
import com.aurora.internalservice.InternalService;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

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
     * @param extractImages True if images also need to be extracted, false otherwise
     * @return The extracted content from the file
     */
    public ExtractedText processFile(InputStream file, String fileRef, String type,
                                     boolean extractImages)
            throws FileTypeNotSupportedException, DocumentNotSupportedException {
        ExtractedText extractedText;
        TextExtractor extractor = fileFormatExtractorMap.get(type);
        if (extractor != null) {
            extractedText = extractor.extract(file, fileRef, extractImages);
            try {
                Objects.requireNonNull(file).close();
            } catch (IOException e) {
                Log.e("FILE_CLOSE", "Failed to close the file: " + fileRef, e);
            }
        } else {
            Log.d("InternalTextProcessor", "File type not supported");
            throw new FileTypeNotSupportedException("You have opened a file with type: " + type +
                    "\n This type is not supported.");
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
