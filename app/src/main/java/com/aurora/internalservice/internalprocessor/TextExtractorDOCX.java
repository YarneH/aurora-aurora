package com.aurora.internalservice.internalprocessor;

import android.util.Log;

import java.io.InputStream;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;

public class TextExtractorDOCX implements TextExtractor {

    /**
     * Extracts the text from a .txt file.
     * @param fileRef a reference to where the file can be found
     * @return an ExtractedText object without title and one line per paragraph.
     */
    @Override
    public ExtractedText extract(String fileRef) {

        return null;
    }


    // TODO combine this with extract(String fileRef)
    public ExtractedText extract(InputStream inputStream, String fileRef) {

        try {
            try {
                XWPFDocument doc = new XWPFDocument(inputStream);
                try {
                    for(XWPFParagraph paragraph : doc.getParagraphs()) {

                        Log.d("JEROEN", paragraph.getText());
                    }
                } finally {
                    doc.close();
                }
            } finally {
                inputStream.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
