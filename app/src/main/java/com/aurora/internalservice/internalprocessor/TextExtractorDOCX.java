package com.aurora.internalservice.internalprocessor;

import android.util.Log;

import java.io.InputStream;
import java.util.List;

import org.apache.poi.xwpf.usermodel.IBodyElement;
import org.apache.poi.xwpf.usermodel.ICell;
import org.apache.poi.xwpf.usermodel.IRunElement;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFSDT;
import org.apache.poi.xwpf.usermodel.XWPFSDTCell;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;

public class TextExtractorDOCX implements TextExtractor {

    /**
     * Extracts the text from a .docx file.
     * @param file      InputStream to the file
     * @param fileRef   a reference to where the file can be found
     * @return an ExtractedText object without title and one line per paragraph.
     */
    @Override
    public ExtractedText extract(InputStream file, String fileRef) {
        ExtractedText extractedText = new ExtractedText();

        Boolean titleFound = false;
        try {
            try {
                try (XWPFDocument doc = new XWPFDocument(file)) {

                    // Process all body elements
//                    for (IBodyElement e : doc.getBodyElements()) {
//                        appendBodyElementText(extractedText, e, doc);
//                    }

                    // TODO Implement extracting images from .docx
                    // TODO Write better logic to extract the title and parse the file
                    for (XWPFParagraph paragraph : doc.getParagraphs()) {
                        String textInParagraph = paragraph.getText();

                        if(textInParagraph.contains("\t")){
                            String[] splitted = textInParagraph.split("\t");
                            for (String split : splitted) {
                                if (!split.replaceAll("[\\r\\n]+", "").isEmpty()) {
                                    extractedText.addParagraph(split.trim());
                                }
                            }
                        } else {
                            if (!textInParagraph.replaceAll("[\\r\\n]+", "").isEmpty()) {

                                if (!titleFound) {
                                    extractedText.setTitle(textInParagraph
                                            .replaceAll("[\\r\\n]+", "")
                                            .trim());
                                    titleFound = true;
                                } else {
                                    extractedText.addParagraph(
                                            textInParagraph
                                                    .replaceAll("[\\r\\n]+", "")
                                                    .trim());
                                }
                            }
                        }
                    }
                }
            } finally {
                file.close();
            }
        } catch (Exception e) {
            // TODO Do something meaningful
            Log.e("EXTRACT_DOCX", "extract in TextExtractorDOCX failed to parse: " + fileRef);
        }

        return extractedText;
    }

    private void appendBodyElementText(ExtractedText text, IBodyElement e, XWPFDocument document) {
        if (e instanceof XWPFParagraph) {
            appendParagraphText(text, (XWPFParagraph) e, document);
        } else if (e instanceof XWPFTable) {
            appendTableText(text, (XWPFTable) e);
        } else if (e instanceof XWPFSDT) {
            text.addParagraph(((XWPFSDT) e).getContent().getText());
        }
    }

    private void appendParagraphText(ExtractedText text, XWPFParagraph paragraph, XWPFDocument document) {
        for (IRunElement run : paragraph.getRuns()) {
            if (run instanceof XWPFRun) {
                text.addParagraph(((XWPFRun)run).text());
            } else {
                Log.d("DOCX_RUN",run.toString());
                text.addParagraph(run.toString());
            }
        }
    }

    private void appendTableText(ExtractedText text, XWPFTable table) {
        //this works recursively to pull embedded tables from tables
        for (XWPFTableRow row : table.getRows()) {
            List<ICell> cells = row.getTableICells();
            for (int i = 0; i < cells.size(); i++) {
                ICell cell = cells.get(i);
                if (cell instanceof XWPFTableCell) {
                    text.addParagraph(((XWPFTableCell) cell).getTextRecursively());
                } else if (cell instanceof XWPFSDTCell) {
                    text.addParagraph(((XWPFSDTCell) cell).getContent().getText());
                }
                if (i < cells.size() - 1) {
                    text.addParagraph("\t");
                }
            }
            text.addParagraph("\n");
        }
    }
}
