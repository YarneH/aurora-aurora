package com.aurora.internalservice.internalprocessor;

import android.util.Base64;
import android.util.Log;

import com.aurora.auroralib.ExtractedText;
import com.aurora.auroralib.Section;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.poi.xwpf.usermodel.IBodyElement;
import org.apache.poi.xwpf.usermodel.ICell;
import org.apache.poi.xwpf.usermodel.IRunElement;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFPicture;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFSDT;
import org.apache.poi.xwpf.usermodel.XWPFSDTCell;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;

public class TextExtractorDOCX implements TextExtractor {

    /**
     * Constant for the maximum number of empty paragraphs in a row
     */
    private static final int MAX_NUMBER_OF_EMPTY_SECTIONS = 2;

    /**
     * Extracted text object
     */
    private ExtractedText mExtractedText = null;

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
     *
     * @param file      InputStream to the file
     * @param fileRef   a reference to where the file can be found
     * @return ExtractedText object with title and sections.
     */
    @Override
    public ExtractedText extract(InputStream file, String fileRef) {
        mExtractedText = new ExtractedText(fileRef, null);

        // Set the values to defaults
        mEmptySectionCount = 0;
        mSectionInProgress = null;
        mLastSeenParagraphLevel = 0;
        mPreviousRunSize = 0;

        try (XWPFDocument doc = new XWPFDocument(file)) {

            for (IBodyElement bodyElement : doc.getBodyElements()) {
                if (bodyElement instanceof XWPFParagraph) {
                    appendParagraphText((XWPFParagraph) bodyElement);
                } else if (bodyElement instanceof XWPFTable) {
                    appendTableText((XWPFTable) bodyElement);
                } else if (bodyElement instanceof XWPFSDT) {
                    mExtractedText.addSimpleSection(((XWPFSDT) bodyElement).getContent().getText());
                }
            }
        } catch (IOException e) {
            Log.e("EXTRACT_DOCX",
                    "a problem occurred while reading the file as a docx: " + fileRef, e);

        } finally {
            // Flush section in progress
            if(mSectionInProgress != null) {
                mExtractedText.addSection(mSectionInProgress);
            }
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
    //I suppress these warnings because there is no easy way to simplify or split this logic into
    // multiple methods without making it harder to understand.
    @java.lang.SuppressWarnings({"squid:MethodCyclomaticComplexity","squid:S3776"})
    private void addRun(String run, int paragraphLevel, int runSize, List<byte[]> images) {
        String formatted = run.trim();

        // Convert the images to Base64
        List<String> encodedImages = new ArrayList<>();
        for (byte[] image: images) {
            encodedImages.add(Base64.encodeToString(image, Base64.DEFAULT));
        }

        // No more than 2 empty sections in a row and not in the beginning of the document
        if(mEmptySectionCount < MAX_NUMBER_OF_EMPTY_SECTIONS && formatted.isEmpty()
                && mExtractedText.getSections() != null && encodedImages.isEmpty()) {
            mExtractedText.addSimpleSection("");
            mEmptySectionCount++;
        } else if(!formatted.isEmpty() || !encodedImages.isEmpty()) {
            // This is the default flow. The section either contains text or images or both.

            // Reset the empty section count
            mEmptySectionCount = 0;

            // Determine if a title, first line is always a title for simplicity
            if(mExtractedText.getTitle() == null && !formatted.isEmpty()) {
                mExtractedText.setTitle(formatted);

                // The title section contains images, add them to their own section.
                if(!encodedImages.isEmpty()) {
                    Section imageSection = new Section();
                    imageSection.setImages(encodedImages);
                    mExtractedText.addSection(imageSection);
                }

                // If the font size is larger than 0 keep track of this. If the built in word
                // headers are used, font size is -1 for some reason.
                if(runSize>0) {
                    mPreviousRunSize = runSize;
                }
                return;
            }

            // Check if we find a (sub)title, when using default word headers most of the headers
            // should be recognized. When not using this, it's more vague, some titels will be
            // found, others not because we need to base us on what was seen previously.
            if (paragraphLevel >= 0 || mPreviousRunSize < runSize) {
                // Flush the previous section, a title will always create a new section
                if (mSectionInProgress != null) {
                    mExtractedText.addSection(mSectionInProgress);
                }

                // Create a new Section
                mSectionInProgress = new Section();
                mSectionInProgress.setTitle(formatted);
                mSectionInProgress.setImages(encodedImages);

                // If the default headers of word are used, we can extract a paragraph level, non
                // header sections will get the last seen paragraph level.
                if(paragraphLevel >= 0) {
                    mSectionInProgress.setLevel(paragraphLevel);
                    mLastSeenParagraphLevel = paragraphLevel;
                } else {
                    // In case the default headers are not used, it is to hard to determine the
                    // level of a paragraph. Just set them all to default level 0.
                    mSectionInProgress.setLevel(0);
                    mLastSeenParagraphLevel = 0;
                    mPreviousRunSize = runSize;
                }
                return;
            }

            // Run is not a (sub) title
            // Check if there is an already a section in progress because a title was found.
            if(mSectionInProgress != null) {
                mSectionInProgress.setBody(formatted);
                mSectionInProgress.getImages().addAll(encodedImages);
            } else {
                mSectionInProgress = new Section(formatted);
                mSectionInProgress.setLevel(mLastSeenParagraphLevel);
                mSectionInProgress.setImages(encodedImages);
            }
            // Not title section are added immediately because nothing could be added anymore.
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
    //I suppress these warnings because there is no easy way to simplify or split this logic into
    // multiple methods without making it harder to understand.
    @java.lang.SuppressWarnings({"squid:MethodCyclomaticComplexity","squid:S3776"})
    private void appendParagraphText(XWPFParagraph paragraph) {
        // For some reason runs can be split randomly, even in the middle of sentences or words.
        // This code is an attempt to combine such runs to one coherent piece of text.

        /* Text that has yet to be added */
        StringBuilder textInProgress = null;
        /* Parameters of the first run of textInProgress, assume the other runs have more or less
         the same parameters */
        XWPFRun runInProgress = null;

        /* List of images that has yet to be added */
        List<byte[]> images = new ArrayList<>();

        // Loop over all the runs in a single paragraph.
        for (IRunElement run : paragraph.getRuns()) {
            Log.d("DOCX_RUN",run.toString());
            // Normal flow
            if (run instanceof XWPFRun) {
                // Extract the images from the run and add them to the list of yet to process images
                List<XWPFPicture> piclist = ((XWPFRun) run).getEmbeddedPictures();
                for (XWPFPicture image: piclist) {
                    images.add(image.getPictureData().getData());
                }

                XWPFRun currentRun = (XWPFRun) run;

                //Loop over all breaks and tabs. This certainly signifies the end of a section.
                for (String text: currentRun.text().split("(?<=\n|\t)")) {
                    // A section ends with a tab or an newline.
                    if((text.endsWith("\t") || text.endsWith("\n"))) {
                        if(textInProgress != null) {
                            textInProgress.append(text);
                        } else {
                            textInProgress = new StringBuilder(text);
                            runInProgress = currentRun;
                        }

                        // Add the section and reset the state variables
                        addRun(textInProgress.toString(), getLevel(paragraph)
                                , runInProgress.getFontSize(), images);

                        images = new ArrayList<>();
                        textInProgress = null;
                        runInProgress = null;
                    } else if(textInProgress != null) {
                        // Build upon the previous run and the section has not ended.
                        textInProgress.append(text);
                    } else if (!"".equals(text.trim())) {
                        // There is no previous run and the section has not ended.
                        runInProgress = currentRun;
                        textInProgress = new StringBuilder(text);
                    } else {
                        // The String is whitespace and immediately added, state is maintained.
                        addRun(currentRun.text(), getLevel(paragraph), currentRun.getFontSize(),
                                new ArrayList<>());
                    }
                }
            } else {
                // The paragraph does not consist of runs. Not much can be done except the adding
                // the raw text of the paragraph.
                addRun(run.toString(), getLevel(paragraph), -1, new ArrayList<>());
            }
        }
        // Flush the last run and any images that are not yet pushed.
        if(runInProgress != null) {
            addRun(textInProgress.toString(), getLevel(paragraph)
                    , runInProgress.getFontSize(), images);
        } else if(!images.isEmpty()) {
            addRun("",getLevel(paragraph), -1, images);
        }
    }

    /**
     * Private method to get heading level of a paragraph. Style of paragraph is used because
     * there is no easy way to get the style of a run and only in very specific cases is the heading
     * style specified in the run.
     *
     * @param paragraph paragraph to determine level of
     * @return -1 if no level is found, otherwise level starting at 0 for title
     */
    private int getLevel(XWPFParagraph paragraph) {
        String paragraphStyle = paragraph.getStyle();

        int level = -1;

        if(paragraphStyle == null) {
            return -1;
        } else if("Titel".equals(paragraphStyle) || "Title".equals(paragraphStyle)) {
            level = 0;
        } else if(paragraphStyle.contains("Heading") || paragraphStyle.contains("Kop")) {
            Pattern p = Pattern.compile("[0-9]+$");
            Matcher m = p.matcher(paragraphStyle);
            if(m.find()) {
                level = Integer.parseInt(m.group());
            }
        }
        return level;
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
