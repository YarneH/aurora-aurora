package com.aurora.internalservice.internalprocessor;

import android.util.Log;

import com.aurora.auroralib.ExtractedText;
import com.aurora.internalservice.internalprocessor.pdfparsing.PDFContentExtractor;
import com.aurora.internalservice.internalprocessor.pdfparsing.ParsedPDF;
import com.itextpdf.text.pdf.PdfReader;

import java.io.IOException;
import java.io.InputStream;

public class TextExtractorPDF implements TextExtractor {

    /**
     * @param fileRef       a reference to where the file can be found
     * @param extractImages True if images need to be extracted, false otherwise
     * @return the extracted text from the file on fileRef
     * @throws DocumentNotSupportedException if the document cannot be processed
     */
    @Override
    public ExtractedText extract(InputStream file, String fileRef, boolean extractImages)
            throws DocumentNotSupportedException {
        PDFContentExtractor reader = new PDFContentExtractor();
        PdfReader pdfreader;
        ParsedPDF parsedPDF = new ParsedPDF();
        try {
            pdfreader = new PdfReader(file);
            //This will convert a Tagged PDF to XML
            reader.extractContent(pdfreader, parsedPDF);
        } catch (IOException e) {
            Log.e("IOexception PDF Reader:",
                    "Error opening and reading the pdf file: " + e.getLocalizedMessage(), e);
        }
        return parsedPDF.toExtractedText(fileRef);
    }
}
