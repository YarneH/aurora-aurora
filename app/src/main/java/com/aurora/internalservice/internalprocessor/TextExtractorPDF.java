package com.aurora.internalservice.internalprocessor;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Iterator;
import java.util.regex.Pattern;

import com.aurora.auroralib.ExtractedText;
import com.aurora.auroralib.Section;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.TaggedPdfReaderTool;

public class TextExtractorPDF implements TextExtractor {
    /**
     * TODO: This method will extract the text using iText
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
            e.printStackTrace();
        }
        String[] xmlLines = baos.toString().split("\n");
        ExtractedText extractedText = new ExtractedText("demoo11.pdf", Calendar.getInstance().getTime());
        Iterator<String> iterable = Arrays.asList(xmlLines).iterator();
        boolean lineProcessedIteration;
        while (iterable.hasNext()) {
            lineProcessedIteration = false;
            String line = iterable.next();
            Section section = new Section();
            if (Pattern.matches("^<[h,H][0-9]>.*", line)) {
                lineProcessedIteration = true;
                String title = line.substring(4);
                section.setLevel(line.charAt(2) - 48);
                while (!Pattern.matches(".*</[h, H]" + String.valueOf(section.getLevel()) + ">$", line)
                        && iterable.hasNext()) {
                    line = iterable.next();
                    title += line;
                }
                title = title.substring(0, title.length() - 5);
                section.setTitle(title);
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
                String paragraph;
                paragraph = line.substring(3);

                while (!Pattern.matches(".*</[p,P]>$", line) && iterable.hasNext()) {
                    line = iterable.next();
                    paragraph += line;
                }
                paragraph = paragraph.substring(0, paragraph.length() - 4);
                section.setBody(paragraph);
            }
            if (lineProcessedIteration){
                extractedText.addSection(section);
            }

        }
        return extractedText;
    }

    private String stripEmptyParagraphs(Iterator<String> iterable, String line) {
        while (Pattern.matches("^<[P, p]> </[P,p]>$",line)) {
            if (iterable.hasNext()) {
                line = iterable.next();
            } else return "";
        }
        ;
        return line;
    }
}
