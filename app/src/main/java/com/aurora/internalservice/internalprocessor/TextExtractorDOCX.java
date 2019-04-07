package com.aurora.internalservice.internalprocessor;

import android.util.Log;

import com.aurora.auroralib.ExtractedText;

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

    private boolean previousLineEmpty = false;
    /**
     * Extracts the text from a .docx file.
     * @param file      InputStream to the file
     * @param fileRef   a reference to where the file can be found
     * @return an ExtractedText object without title and one line per paragraph.
     */
    @Override
    public ExtractedText extract(InputStream file, String fileRef) {
        ExtractedText extractedText = new ExtractedText(fileRef, null);

        try {
            try (XWPFDocument doc = new XWPFDocument(file)) {

                // Code that might be useful for tables
                //for (IBodyElement e : doc.getBodyElements()) { //NOSONAR
                  //  appendBodyElementText(extractedText, e); //NOSONAR
                //} //NOSONAR

                // TODO Implement extracting images from .docx
                // TODO Write better logic to extract the title and parse the file
                for (XWPFParagraph paragraph : doc.getParagraphs()) {
                    Log.d("DOCX", "Para: " + paragraph.getText());

                    String textInParagraph = paragraph.getText();

                    if(textInParagraph.contains("\t")){
                        for (String split : textInParagraph.split("\t")) {
                            addParagraph(extractedText, split, false);
                        }
                    } else if (extractedText.getTitle() == null && !formatParagraph(textInParagraph).isEmpty()) {
                        extractedText.setTitle(formatParagraph(textInParagraph));
                    } else {
                        addParagraph(extractedText, textInParagraph, true);
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

    /**
     * Appends the paragraph to the extractedtextobject and takes care of formatting
     *
     * @param extractedText     an extractedText object to append to
     * @param paragraph         the paragraph that will be appended
     * @param emptyLineAllowed    boolean that tells if adding an empty line is allowed
     */
    private void addParagraph(ExtractedText extractedText, String paragraph, boolean emptyLineAllowed) {
        String formatted = formatParagraph(paragraph);

        if(!previousLineEmpty || !formatted.isEmpty()) {
            if(formatted.isEmpty()) {
                if(extractedText.getSections() != null
                        && !extractedText.getSections().isEmpty()
                        && emptyLineAllowed) {
                    formatted = "\n";
                    previousLineEmpty = true;
                    extractedText.addSimpleSection(formatted);
                }
            } else {
                previousLineEmpty = false;
                extractedText.addSimpleSection(formatted);
            }
        }
    }

    /**
     * Formats the paragraph to not contain leading and trailing spaces and carriage returns.
     *
     * @param paragraph     the paragraph that needs to be formatted
     * @return              String that is formatted
     */
    private static String formatParagraph(String paragraph) {
        return paragraph.replaceAll("[\\r\\n]+", "").trim();
    }

    // Not used
    private void appendBodyElementText(ExtractedText text, IBodyElement e) {
        if (e instanceof XWPFParagraph) {
            appendParagraphText(text, (XWPFParagraph) e);
        } else if (e instanceof XWPFTable) {
            appendTableText(text, (XWPFTable) e);
        } else if (e instanceof XWPFSDT) {
            text.addSimpleSection(((XWPFSDT) e).getContent().getText());
        }
    }

    // Not used
    private void appendParagraphText(ExtractedText text, XWPFParagraph paragraph) {
        for (IRunElement run : paragraph.getRuns()) {
            if (run instanceof XWPFRun) {
                text.addSimpleSection(((XWPFRun)run).text());
            } else {
                Log.d("DOCX_RUN",run.toString());
                text.addSimpleSection(run.toString());
            }
        }
    }

    // Not used
    private void appendTableText(ExtractedText text, XWPFTable table) {
        //this works recursively to pull embedded tables from tables
        for (XWPFTableRow row : table.getRows()) {
            List<ICell> cells = row.getTableICells();
            for (int i = 0; i < cells.size(); i++) {
                ICell cell = cells.get(i);
                if (cell instanceof XWPFTableCell) {
                    text.addSimpleSection(((XWPFTableCell) cell).getTextRecursively());
                } else if (cell instanceof XWPFSDTCell) {
                    text.addSimpleSection(((XWPFSDTCell) cell).getContent().getText());
                }
                if (i < cells.size() - 1) {
                    text.addSimpleSection("\t");
                }
            }
            text.addSimpleSection("\n");
        }
    }
}
