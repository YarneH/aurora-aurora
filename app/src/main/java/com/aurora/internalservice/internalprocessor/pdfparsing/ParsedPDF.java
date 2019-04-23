package com.aurora.internalservice.internalprocessor.pdfparsing;


import com.aurora.auroralib.ExtractedText;
import com.aurora.auroralib.Section;

import java.util.ArrayList;
import java.util.List;

public class ParsedPDF {
    // Contains all the extracted content from the file in an array
    private ArrayList<PDFStructureElement> pdfElements;
    // If a header was ever added, the parsing strategy changes
    private boolean mContainsHeaders;
    // The extracted text (instantiated after call toExtractedText)
    private ExtractedText mExtractedText;

    public ParsedPDF() {
        pdfElements = new ArrayList<>();
        mContainsHeaders = false;
    }

    public ExtractedText toExtractedText(ExtractedText extractedText) {
        mExtractedText = extractedText;
        if (mContainsHeaders) {
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
        while (index < pdfElements.size()) {
            Section section = new Section();
            if (pdfElements.get(index).getType().equals(HeadingFromPDF.TYPE)) {
                section.setTitle(pdfElements.get(index).getContent());
                section.setLevel(pdfElements.get(index).getLevel());
                index++;
                index = skipEmptyLines(index);
            }
            while (index < pdfElements.size() &&
                    !pdfElements.get(index).getType().equals(HeadingFromPDF.TYPE)) {
                addElementToSection(pdfElements.get(index), section);
                index++;
            }
            mExtractedText.addSection(section);
            index = skipEmptyLines(index);
        }
    }

    private void addElementToSection(PDFStructureElement pdfStructureElement, Section section) {
        if (pdfStructureElement.getType().equals(ImageFromPDF.TYPE)) {
            List<String> images = new ArrayList<>();
            images.add(pdfStructureElement.getContent());
            section.addImages((images));
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

        while (index < pdfElements.size()) {
            Section section = new Section();
            StringBuilder body = new StringBuilder();
            if (index + 1 < pdfElements.size() && pdfElements.get(index + 1).getContent().trim().isEmpty()) {
                section.setTitle(pdfElements.get(index).getContent());
                index = skipEmptyLines(index + 1);
            }
            while (!pdfElements.get(index).getContent().trim().isEmpty() && index < pdfElements.size()) {
                body.append(pdfElements.get(index).getContent());
                index++;
            }
            mExtractedText.getSections().add(section);
        }
    }

    private int searchTitle() {
        int currentLine = skipEmptyLines(0);
        if (pdfElements.get(currentLine).getType().equals(ParagraphFromPDF.TYPE)) {
            int nextline = skipEmptyLines(currentLine + 1);
            if (pdfElements.get(nextline).getType().equals(HeadingFromPDF.TYPE)) {
                mExtractedText.setTitle(pdfElements.get(currentLine).getContent());
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
        if (startIndex < pdfElements.size()) {
            String line = pdfElements.get(startIndex).getContent();
            while (startIndex < pdfElements.size() &&
                    (!pdfElements.get(startIndex).getType().equals(ImageFromPDF.TYPE)) &&
                    line.trim().isEmpty()) {
                startIndex++;
                line = pdfElements.get(startIndex).getContent();
            }
        }
        return startIndex;
    }


    public void addHeader(String text, int level) {
        mContainsHeaders = true;
        pdfElements.add(new HeadingFromPDF(text, level));
    }


    public void addParagraph(String text) {
        pdfElements.add(new ParagraphFromPDF(text));
    }


    public void addImage(String image) {
        pdfElements.add(new ImageFromPDF(image));
    }
}
