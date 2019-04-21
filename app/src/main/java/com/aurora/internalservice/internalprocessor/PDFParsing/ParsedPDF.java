package com.aurora.internalservice.internalprocessor.PDFParsing;


import com.aurora.auroralib.ExtractedText;
import com.aurora.auroralib.Section;

import java.util.ArrayList;

public class ParsedPDF {
    // Contains all the extracted content from the file in an array
    private ArrayList<PDFStructureElement> parsedPDF;
    // If a header was ever added, the parsing strategy changes
    private boolean mContains_Headers;
    // The extracted text (instantiated after call toExtractedText)
    private ExtractedText mExtractedText;

    public ParsedPDF() {
        parsedPDF = new ArrayList<>();
        mContains_Headers = false;
    }

    public ExtractedText toExtractedText(ExtractedText extractedText) {
        mExtractedText = extractedText;
        if (mContains_Headers) {
            toExtractedTextWithHeaders();
        } else {
            toExtractedTextWithoutHeaders();
        }
        return extractedText;
    }

    /**
     * This will convert the text to an ExtractedText if Headers where found during extraction
     */

    private void toExtractedTextWithHeaders() {
        int index = searchTitle();
        while (index < parsedPDF.size()) {
            Section section = new Section();
            if (parsedPDF.get(index).getType().equals(HeadingFromPDF.TYPE)) {
                section.setTitle(parsedPDF.get(index).getContent());
                section.setLevel(parsedPDF.get(index).getLevel());
                index++;
                index = skipEmptyLines(index);
            }
            while (index < parsedPDF.size() && !parsedPDF.get(index).getType().equals(HeadingFromPDF.TYPE)) {
                addElementToSection(parsedPDF.get(index), section);
                index++;
            }
            index = skipEmptyLines(index);
        }
    }

    private void addElementToSection(PDFStructureElement pdfStructureElement, Section section) {
        if (pdfStructureElement.getType().equals(ImageFromPDF.TYPE)) {
            section.getImages().add(pdfStructureElement.getContent());
        } else {
            section.setBody(section.getBody() + "\n" + pdfStructureElement.getContent());
        }
    }

    /**
     * This will convert the text to an ExtractedText when no headers where found during extraction
     * Since there are no headers, no title is found
     */
    private void toExtractedTextWithoutHeaders() {
        int index = skipEmptyLines(0);

        while (index < parsedPDF.size()) {
            Section section = new Section();
            StringBuilder body = new StringBuilder();
            if (index + 1 < parsedPDF.size() && parsedPDF.get(index + 1).getContent().trim().isEmpty()) {
                section.setTitle(parsedPDF.get(index).getContent());
                index = skipEmptyLines(index + 1);
            }
            while (!parsedPDF.get(index).getContent().trim().isEmpty() && index < parsedPDF.size()) {
                body.append(parsedPDF.get(index).getContent());
                index++;
            }
            mExtractedText.getSections().add(section);
        }
    }

    private int searchTitle() {
        int currentLine = skipEmptyLines(0);
        if (parsedPDF.get(currentLine).getType().equals(ParagraphFromPDF.TYPE)) {
            int nextline = skipEmptyLines(currentLine + 1);
            if (parsedPDF.get(nextline).getType().equals(HeadingFromPDF.TYPE)) {
                mExtractedText.setTitle(parsedPDF.get(currentLine).getContent());
                return nextline;
            }
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
        if (startIndex < parsedPDF.size()) {
            String line = parsedPDF.get(startIndex).getContent();
            while (startIndex < parsedPDF.size() && (!parsedPDF.get(startIndex).getType().equals(ImageFromPDF.TYPE)) &&
                    line.trim().isEmpty()) {
                startIndex++;
                line = parsedPDF.get(startIndex).getContent();
            }
        }
        return startIndex;
    }


    public void addHeader(String text, int level) {
        mContains_Headers = true;
        parsedPDF.add(new HeadingFromPDF(text, level));
    }


    public void addParagraph(String text) {
        parsedPDF.add(new ParagraphFromPDF(text));
    }


    public void addImage(String image) {
        parsedPDF.add(new ImageFromPDF(image));
    }
}
