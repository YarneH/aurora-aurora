package com.aurora.internalservice.internalprocessor;

import android.util.Log;

import com.aurora.auroralib.ExtractedText;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;

public class TextExtractorDOCX implements TextExtractor {

    /**
     * Extracts the text from a .docx file.
     * @param file      InputStream to the file
     * @param fileRef   a reference to where the file can be found
     * @return an ExtractedText object without title and one line per paragraph.
     */
    @Override
    public ExtractedText extract(InputStream file, String fileRef) {
        ExtractedText extractedText = null;

        try {
            try {
                try (XWPFDocument doc = new XWPFDocument(file)) {
                    List<String> paragraphs = new ArrayList<>();

                    // TODO Write actual logic to extract the title and parse the file
                    for (XWPFParagraph paragraph : doc.getParagraphs()) {
                        paragraphs.add(paragraph.getText());

                        // TODO Remove unnecessary Log
                        Log.d("DOCX", paragraph.getText());
                    }

                    // TODO Implement extracting images from .docx

                    extractedText = new ExtractedText("", paragraphs);
                }
            } finally {
                file.close();
            }
        } catch (Exception e) {
            // TODO Do something meaningful
            Log.d("EXTRACT_DOCX", "extract in TextExtractorDOCX failed to parse: " + fileRef);
        }

        return extractedText;
    }
}
