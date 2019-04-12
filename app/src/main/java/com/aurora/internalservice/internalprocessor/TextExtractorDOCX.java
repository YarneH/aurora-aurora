package com.aurora.internalservice.internalprocessor;

import android.util.Log;

import com.aurora.auroralib.ExtractedText;
import com.aurora.auroralib.Section;

import java.io.InputStream;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
     * Extracted text object
     */
    private ExtractedText mExtractedText;

    /**
     * A counter that keeps track of the number of empty sections in a row
     */
    private int mEmptySectionCount = 0;

    /**
     * Section that still needs to be added to extractedText
     */
    private Section mSectionInProgress = null;

    /**
     * Runs where no paragraph level can be found will get this level
     */
    private int mLastSeenParagraphLevel = 0;

    /**
     * Array that keeps track of the sizes of titles to determine the level
     */
    private int mPreviousRunSize = 0;


    /**
     * Extracts the text from a .docx file.
     * @param file      InputStream to the file
     * @param fileRef   a reference to where the file can be found
     * @return an ExtractedText object without title and one line per paragraph.
     */
    @Override
    public ExtractedText extract(InputStream file, String fileRef) {
        mExtractedText = new ExtractedText(fileRef, null);

        // Set the values to defaults
        mEmptySectionCount = 0;
        mSectionInProgress = null;
        mLastSeenParagraphLevel = 0;
        mPreviousRunSize = 0;


        try {
            try (XWPFDocument doc = new XWPFDocument(file)) {

                for (IBodyElement e : doc.getBodyElements()) {
                    if (e instanceof XWPFParagraph) {
                        appendParagraphText((XWPFParagraph) e);
                    } else if (e instanceof XWPFTable) {
                        appendTableText((XWPFTable) e);
                    } else if (e instanceof XWPFSDT) {
                        mExtractedText.addSimpleSection(((XWPFSDT) e).getContent().getText());
                    }
                }

                // TODO Implement extracting images from .docx

            } finally {
                file.close();
                // Flush section in progress
                if(mSectionInProgress != null) {
                    mExtractedText.addSection(mSectionInProgress);
                }
            }
        } catch (Exception e) {
            Log.e("EXTRACT_DOCX", "extract in TextExtractorDOCX failed to parse: " + fileRef);
        }

        return mExtractedText;
    }

    /**
     * Appends the String run to the extractedText object. State is maintained in order to
     * estimate if the run is a title. The first run is always the title.
     *
     * @param run               String that needs to be added to extractedText
     * @param paragraphLevel    If a level can be extracted from the parent paragraph, this will
     *                          be for multilevel titles.
     * @param runSize           If the paragraphlevel cannot be used, the textsize is used to
     *                          determine (sub)titles. Only one level of titles is supported in
     *                          this case.
     */
    private void addRun(String run, int paragraphLevel, int runSize) {
        String formatted = run.trim();//formatParagraph(run);

        // No more than 2 empty sections in a row and not in the beginning of the document
        if(mEmptySectionCount < 2 && formatted.isEmpty() && mExtractedText.getSections() != null) {
            mExtractedText.addSimpleSection("");
            mEmptySectionCount++;
        } else if(!formatted.isEmpty()) {
            mEmptySectionCount = 0;

            // Determine if a title, first line is always a title for simplicity
            if(mExtractedText.getTitle() == null) {
                mExtractedText.setTitle(formatted);
                if(runSize>0) {
                    mPreviousRunSize = runSize;
                }
                return;
            }

            // Check if we find a (sub)title
            if (paragraphLevel >= 0 || mPreviousRunSize < runSize) {
                // Flush the previous section
                if (mSectionInProgress != null) {
                    mExtractedText.addSection(mSectionInProgress);
                }

                // Create a new Section
                mSectionInProgress = new Section();
                mSectionInProgress.setTitle(formatted);
                if(paragraphLevel >= 0) {
                    mSectionInProgress.setLevel(paragraphLevel);
                    mLastSeenParagraphLevel = paragraphLevel;
                } else {
                    mSectionInProgress.setLevel(0);
                    mLastSeenParagraphLevel = 0;
                    mPreviousRunSize = runSize;
                }
                return;
            }

            // Run is not a (sub) title
            // Check if there is an already a section in progress
            if(mSectionInProgress != null) {
                mSectionInProgress.setBody(formatted);
            } else {
                mSectionInProgress = new Section(formatted);
                mSectionInProgress.setLevel(mLastSeenParagraphLevel);
            }
            // Flush now
            mExtractedText.addSection(mSectionInProgress);
            mSectionInProgress = null;
            mPreviousRunSize = runSize;
        }
    }

    /**
     * Private method that is used to add the different runs of a paragraph to the extractedText
     * object. Runs in a paragraph can start and stop in the middle of words. So there is state
     * maintained to add them back together. New sections are started when a tab or newline is
     * found.
     *
     * @param paragraph Object of Apache POI representing a docx paragraph.
     */
    private void appendParagraphText(XWPFParagraph paragraph) {
        // For some reason runs can be split randomly, even in the middle of sentences or words.
        // This code is an attempt to combine such runs to one coherent piece of text.
        StringBuilder textInProgress = null;
        XWPFRun runInProgress = null;

        for (IRunElement run : paragraph.getRuns()) {
            Log.d("DOCX_RUN",run.toString());
            if (run instanceof XWPFRun) {
                XWPFRun currentRun = (XWPFRun) run;
                String runText = currentRun.text();

                for (String text: runText.split("(?<=\n|\t)")) {
                    // A section ends with a tab or an newline.
                    if((text.endsWith("\t") || text.endsWith("\n"))) {
                        if(textInProgress != null) {
                            textInProgress.append(text);
                        } else {
                            textInProgress = new StringBuilder(text);
                            runInProgress = currentRun;
                        }

                        addRun(textInProgress.toString(), getLevel(paragraph)
                                , runInProgress.getFontSize());

                        textInProgress = null;
                        runInProgress = null;
                    }

                    // Build upon the previous run
                    else if(textInProgress != null) {
                        textInProgress.append(text);
                    }
                    // There is no previous run and the string is not empty, save it.
                    else if (!text.trim().equals("")) {
                        runInProgress = currentRun;
                        textInProgress = new StringBuilder(text);
                    }
                    // The String is whitespace, just flush it, just in case
                    else {
                        addRun(currentRun.text(), getLevel(paragraph), currentRun.getFontSize());
                    }
                }
            } else {
                // The paragraph does not consist of runs.
                addRun(run.toString(), getLevel(paragraph), -1);
            }
        }
        // Flush the last run
        if(runInProgress != null) {
            addRun(textInProgress.toString(), getLevel(paragraph)
                    , runInProgress.getFontSize());
        }
    }

    /**
     * Private method to get heading level of a paragraph
     * @param paragraph paragraph to determine level of
     * @return -1 if no level is found, otherwise level starting at 0 for title
     */
    private int getLevel(XWPFParagraph paragraph) {
        String paragraphStyle = paragraph.getStyle();

        if(paragraphStyle == null) return -1;
        if(paragraphStyle.equals("Titel") || paragraphStyle.equals("Title")) return 0;
        if(paragraphStyle.contains("Heading") || paragraphStyle.contains("Kop")) {
            Pattern p = Pattern.compile("[0-9]+$");
            Matcher m = p.matcher(paragraphStyle);
            if(m.find()) {
                return Integer.parseInt(m.group());
            }
        }
        return  -1;
    }

    /**
     * Private method with very basic approach to extract all text from a table row by row. Every
     * row is a new section and information extracted from rows is tab separated.
     *
     * @param table The table that needs to be extracted
     */
    private void appendTableText(XWPFTable table) {
        //this works recursively to pull embedded tables from tables
        for (XWPFTableRow row : table.getRows()) {
            List<ICell> cells = row.getTableICells();

            StringBuilder line = new StringBuilder();
            for (int i = 0; i < cells.size(); i++) {
                ICell cell = cells.get(i);

                if (cell instanceof XWPFTableCell) {
                    line.append(((XWPFTableCell) cell).getTextRecursively());
                } else if (cell instanceof XWPFSDTCell) {
                    line.append(((XWPFSDTCell) cell).getContent().getText());
                }
                if (i < cells.size() - 1) {
                    line.append("\t");
                }
            }
            mExtractedText.addSimpleSection(line.toString());
        }
    }
}
