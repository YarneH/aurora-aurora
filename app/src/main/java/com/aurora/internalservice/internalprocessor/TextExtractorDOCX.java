package com.aurora.internalservice.internalprocessor;

import android.util.Log;

import java.io.InputStream;

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

        try {
            try {
                try (XWPFDocument doc = new XWPFDocument(file)) {
                    for (XWPFParagraph paragraph : doc.getParagraphs()) {

                        Log.d("JEROEN", paragraph.getText());
                    }
                }
            } finally {
                file.close();
            }
        } catch (Exception e) {
            // TODO Do something meaningfull
            e.printStackTrace();
        }

        return null;
    }
}
