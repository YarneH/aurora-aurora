package com.aurora.internalservice.internalprocessor;

import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Pattern;

import com.aurora.auroralib.ExtractedText;
import com.aurora.auroralib.Section;
import com.aurora.internalservice.internalprocessor.pdfparsing.PDFContentExtractor;
import com.aurora.internalservice.internalprocessor.pdfparsing.ParsedPDF;
import com.itextpdf.text.pdf.PdfDate;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.TaggedPdfReaderTool;

public class TextExtractorPDF implements TextExtractor {

    private static final int CHAR_TO_INT = 48;
    private static final int OFFSET_XML_HEADING_TAGS = 4;
    private static final int OFFSET_XML_P_TAGS = 3;
    private static final int HEADING_LEVEL_INDEX = 2;

    private boolean mLineProcessed;
    private String mCurrentLine;
    private Section mExtractingSection;
    private ExtractedText mExtractedText;
    private Iterator<String> mXMLIterator;

    public TextExtractorPDF() {
        mLineProcessed = false;
        mCurrentLine = "";
    }

    /**
     * @param fileRef a reference to where the file can be found
     * @return the extracted text from the file on fileRef
     */
    public ExtractedText extract_outdated(InputStream file, String fileRef) {
        TaggedPdfReaderTool reader = new TaggedPdfReaderTool();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfReader pdfreader = null;
        //TODO: Handle this so that errors are thrown
        try {
            pdfreader = new PdfReader(file);
            //This will convert a Tagged PDF to XML
            reader.convertToXml(pdfreader, baos);
        } catch (IOException e) {
            Log.e("IOexception PDF Reader:",
                    "Error opening and reading the pdf file: " + e.getLocalizedMessage());
        }
        mExtractedText = new ExtractedText(fileRef, null);
        Map<String, String> info = pdfreader.getInfo();
        if(info.containsKey("ModDate")){
            Date lastEdit = PdfDate.decode(info.get("ModDate")).getTime();
            mExtractedText.setDateLastEdit(lastEdit);
        }
        //Read the XML lines and split on newlines
        String[] xmlLines = baos.toString().split("\n");
        mXMLIterator = Arrays.asList(xmlLines).iterator();

        // As long as the file is not empty, process new lines
        while (mXMLIterator.hasNext()) {
            mLineProcessed = false;
            mCurrentLine = mXMLIterator.next();
            mExtractingSection = new Section();
            extractHeaders();
            stripEmptyParagraphs();
            extractSections();
        }
        return mExtractedText;
    }

    /**
     * Extraction 2 better i hope
     */
    public ExtractedText extract(InputStream file, String fileRef){
        PDFContentExtractor reader = new PDFContentExtractor();
        PdfReader pdfreader = null;
        ParsedPDF parsedPDF = new ParsedPDF();
        try {
            pdfreader = new PdfReader(file);
            //This will convert a Tagged PDF to XML
            reader.extractContent(pdfreader,parsedPDF);
        } catch (IOException e) {
            Log.e("IOexception PDF Reader:",
                    "Error opening and reading the pdf file: " + e.getLocalizedMessage());
        }
        return parsedPDF.toExtractedText(new ExtractedText(fileRef,null));
    }
    /**
     * Extract sections from the pdf
     */
    private void extractSections() {
        if (mXMLIterator.hasNext() && Pattern.matches("^<[p,P]>.*", mCurrentLine)) {
            mLineProcessed = true;
            StringBuilder paragraph;
            paragraph = new StringBuilder(mCurrentLine.substring(OFFSET_XML_P_TAGS));

            while (!Pattern.matches(".*</[p,P]>$", mCurrentLine) && mXMLIterator.hasNext()) {
                mCurrentLine = mXMLIterator.next();
                paragraph.append(mCurrentLine);
            }
            paragraph = new StringBuilder(paragraph.substring(0, paragraph.length() - (OFFSET_XML_P_TAGS + 1)));
            mExtractingSection.setBody(paragraph.toString());
        }
        if (mLineProcessed){
            mExtractedText.addSection(mExtractingSection);
        }
    }

    /**
     * Extract headers from the pdf
     */
    private void extractHeaders(){
        if (Pattern.matches("^<[h,H][0-9]>.*", mCurrentLine)) {
            mLineProcessed = true;
            StringBuilder title = new StringBuilder(mCurrentLine.substring(OFFSET_XML_HEADING_TAGS));
            mExtractingSection.setLevel(mCurrentLine.charAt(HEADING_LEVEL_INDEX) - CHAR_TO_INT);
            while (!Pattern.matches(".*</[h, H]" + String.valueOf(mExtractingSection.getLevel()) + ">$", mCurrentLine)
                    && mXMLIterator.hasNext()) {
                mCurrentLine = mXMLIterator.next();
                title.append(mCurrentLine);
            }
            title = new StringBuilder(title.substring(0, title.length() - (OFFSET_XML_HEADING_TAGS + 1)));
            mExtractingSection.setTitle(title.toString());
            if (mXMLIterator.hasNext()) {
                mCurrentLine = mXMLIterator.next();
            } else {
                mExtractedText.addSection(mExtractingSection);
            }
        }
    }



    /**
     * As long as a paragraph is empty, skip to the next one
     */
    private void stripEmptyParagraphs() {
        while (Pattern.matches("^<[P, p]> </[P,p]>$",mCurrentLine)) {
            if (mXMLIterator.hasNext()) {
                mCurrentLine = mXMLIterator.next();
            }
        }
    }
}
