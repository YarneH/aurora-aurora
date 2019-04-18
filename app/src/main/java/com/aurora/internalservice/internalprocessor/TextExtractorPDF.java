package com.aurora.internalservice.internalprocessor;

import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Iterator;
import java.util.regex.Pattern;

import com.aurora.auroralib.ExtractedText;
import com.aurora.auroralib.Section;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.TaggedPdfReaderTool;

public class TextExtractorPDF implements TextExtractor {

    private static final int CHAR_TO_INT = 48;
    private static final int OFFSET_XML_HEADING_TAGS = 4;
    private static final int OFFSET_XML_P_TAGS = 3;

    /**
     * @param fileRef a reference to where the file can be found
     * @return the extracted text from the file on fileRef
     */
    @Override
    public ExtractedText extract(InputStream file, String fileRef) {
        TaggedPdfReaderTool reader = new TaggedPdfReaderTool();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfReader pdfreader = null;
        try {
            pdfreader = new PdfReader(file);
            reader.convertToXml(pdfreader, baos);
        } catch (IOException e) {
            Log.e("IOexception PDF Reader:",
                    "Error opening and reading the pdf file: " + e.getLocalizedMessage());
        }
        String[] xmlLines = baos.toString().split("\n");
        ExtractedText extractedText = new ExtractedText(fileRef, null);
        Iterator<String> iterable = Arrays.asList(xmlLines).iterator();
        boolean lineProcessedIteration;
        while (iterable.hasNext()) {
            lineProcessedIteration = false;
            String line = iterable.next();
            Section section = new Section();
            if (Pattern.matches("^<[h,H][0-9]>.*", line)) {
                lineProcessedIteration = true;
                StringBuilder title = new StringBuilder(line.substring(OFFSET_XML_HEADING_TAGS));
                section.setLevel(line.charAt(2) - CHAR_TO_INT);
                while (!Pattern.matches(".*</[h, H]" + String.valueOf(section.getLevel()) + ">$", line)
                        && iterable.hasNext()) {
                    line = iterable.next();
                    title.append(line);
                }
                title = new StringBuilder(title.substring(0, title.length() - (OFFSET_XML_HEADING_TAGS + 1)));
                section.setTitle(title.toString());
                if (iterable.hasNext()) {
                    line = iterable.next();
                } else {
                    extractedText.addSection(section);
                    return extractedText;
                }
            }
            line = stripEmptyParagraphs(iterable, line);
            if (iterable.hasNext() && Pattern.matches("^<[p,P]>.*", line)) {
                lineProcessedIteration = true;
                StringBuilder paragraph;
                paragraph = new StringBuilder(line.substring(OFFSET_XML_P_TAGS));

                while (!Pattern.matches(".*</[p,P]>$", line) && iterable.hasNext()) {
                    line = iterable.next();
                    paragraph.append(line);
                }
                paragraph = new StringBuilder(paragraph.substring(0, paragraph.length() - (OFFSET_XML_P_TAGS + 1)));
                section.setBody(paragraph.toString());
            }
            if (lineProcessedIteration){
                extractedText.addSection(section);
            }

        }
        return extractedText;
    }

    /**
     * As long as a paragraph is empty, skip to the next one
     * @param iterable the extracted XML
     * @param line the current line
     * @return the next line not containing empty paragraphs
     */
    private static String stripEmptyParagraphs(Iterator<String> iterable, String line) {
        while (Pattern.matches("^<[P, p]> </[P,p]>$",line)) {
            if (iterable.hasNext()) {
                line = iterable.next();
            } else {
                return "";
            }
        }
        return line;
    }
}
