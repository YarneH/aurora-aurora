package com.aurora.internalservice.internalprocessor.pdfparsing;


import com.aurora.auroralib.ExtractedImage;
import com.aurora.auroralib.ExtractedText;
import com.aurora.auroralib.Section;

import java.util.ArrayList;

/**
 * This class will represent a PDF parsed by the {@link PDFContentExtractor}
 * It has a list of {@link PDFStructureElement} and it can be converted to an {@link ExtractedText}
 */
public class ParsedPDF {
    // Contains all the extracted content from the file in an array
    private ArrayList<PDFStructureElement> mPDFElements;
    // If a header was ever added, the parsing strategy changes
    private boolean mContainsHeaders;
    // The extracted text (instantiated after call toExtractedText)
    private ExtractedText mExtractedText;

    public ParsedPDF() {
        mPDFElements = new ArrayList<>();
        mContainsHeaders = false;
    }

    /**
     * Adds structure to the text
     *
     * @param fileUri the uri of the file
     * @param fileRef the name of the file
     * @return the same ExtractedText but now filled with text
     */
    public ExtractedText toExtractedText(String fileUri, String fileRef) {
        mExtractedText = new ExtractedText(fileUri, fileRef);
        if (mContainsHeaders) {
            toExtractedTextWithHeaders();
        } else {
            toExtractedTextWithoutHeaders();
        }
        return mExtractedText;
    }

    /**
     * This will convert the text to an ExtractedText if Headers where found during extraction
     */
    private void toExtractedTextWithHeaders() {
        int index = searchTitle();
        while (index < mPDFElements.size()) {
            Section section = new Section();
            if (mPDFElements.get(index).getType().equals(HeadingFromPDF.TYPE)) {
                section.setTitle(mPDFElements.get(index).getContent());
                section.setLevel(mPDFElements.get(index).getLevel());
                index++;
                index = skipEmptyLines(index);
            }
            while (index < mPDFElements.size() &&
                    !mPDFElements.get(index).getType().equals(HeadingFromPDF.TYPE)) {
                addElementToSection(mPDFElements.get(index), section);
                index++;
            }
            mExtractedText.addSection(section);
            index = skipEmptyLines(index);
        }
    }

    /**
     * Adds an PDFStructureElement to a Section, depending on which type of element it is
     *
     * @param pdfStructureElement The element to add
     * @param section             the section to add the element to
     */
    private void addElementToSection(PDFStructureElement pdfStructureElement, Section section) {
        if (pdfStructureElement.getType().equals(ImageFromPDF.TYPE)) {
            section.addExtractedImage(new ExtractedImage(pdfStructureElement.getContent()));
        } else {
            section.concatBody(pdfStructureElement.getContent() + "\n");
        }
    }

    /**
     * This will convert the text to an ExtractedText when no headers where found during extraction
     * Since there are no headers, no title is found
     */
    private void toExtractedTextWithoutHeaders() {
        int index = skipEmptyLines(0);

        while (index < mPDFElements.size()) {
            Section section = new Section();
            StringBuilder body = new StringBuilder();
            if (index + 1 < mPDFElements.size() && mPDFElements.get(index + 1).getContent().trim().isEmpty()) {
                section.setTitle(mPDFElements.get(index).getContent());
                index = skipEmptyLines(index + 1);
            }
            while (!mPDFElements.get(index).getContent().trim().isEmpty() && index < mPDFElements.size()) {
                body.append(mPDFElements.get(index).getContent());
                index++;
            }
            mExtractedText.getSections().add(section);
        }
    }

    /**
     * Logic to find the title of a file
     *
     * @return the line number after possibly finding a title
     */
    private int searchTitle() {
        int currentLine = skipEmptyLines(0);
        if (mPDFElements.get(currentLine).getType().equals(ParagraphFromPDF.TYPE)) {
            int nextline = skipEmptyLines(currentLine + 1);
            mExtractedText.setTitle(mPDFElements.get(currentLine).getContent());
            return nextline;

        }
        return currentLine;
    }

    /**
     * Skips lines only containing whitespace
     *
     * @param startIndex index from which to start searching for content
     * @return index of list containing content
     */
    private int skipEmptyLines(int startIndex) {
        if (startIndex < mPDFElements.size()) {
            String line = mPDFElements.get(startIndex).getContent();
            while (startIndex < mPDFElements.size() &&
                    (!mPDFElements.get(startIndex).getType().equals(ImageFromPDF.TYPE)) &&
                    line.trim().isEmpty()) {
                startIndex++;
                line = mPDFElements.get(startIndex).getContent();
            }
        }
        return startIndex;
    }


    void addHeader(String text, int level) {
        mContainsHeaders = true;
        mPDFElements.add(new HeadingFromPDF(text, level));
    }


    void addParagraph(String text) {
        mPDFElements.add(new ParagraphFromPDF(text));
    }


    void addImage(String image) {
        mPDFElements.add(new ImageFromPDF(image));
    }
}
