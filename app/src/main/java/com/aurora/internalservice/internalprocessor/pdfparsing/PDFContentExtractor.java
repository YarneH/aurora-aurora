package com.aurora.internalservice.internalprocessor.pdfparsing;

/*
 *
 * This file is part of the iText (R) project.
 * Copyright (c) 1998-2016 iText Group NV
 * Authors: Bruno Lowagie, et al.
 *
 * Jonas Cuypers adapted this code on 23/04
 */

import android.util.Log;

import com.aurora.internalservice.internalprocessor.DocumentNotSupportedException;
import com.itextpdf.text.pdf.PdfArray;
import com.itextpdf.text.pdf.PdfDictionary;
import com.itextpdf.text.pdf.PdfName;
import com.itextpdf.text.pdf.PdfNumber;
import com.itextpdf.text.pdf.PdfObject;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.FilteredTextRenderListener;
import com.itextpdf.text.pdf.parser.MarkedContentRenderFilter;
import com.itextpdf.text.pdf.parser.PdfContentStreamProcessor;
import com.itextpdf.text.pdf.parser.RenderFilter;
import com.itextpdf.text.pdf.parser.SimpleTextExtractionStrategy;
import com.itextpdf.text.pdf.parser.TextExtractionStrategy;

import java.io.IOException;
import java.util.regex.Pattern;

/**
 * Converts a tagged PDF document into a ParsedPDF.
 */
public class PDFContentExtractor {

    private ParsedPDF mParsedPDF;

    /**
     * Subtract 48 from a char to get the number in int
     */
    private static final int CHAR_TO_INT = 48;

    public PDFContentExtractor() {
        mParsedPDF = new ParsedPDF();
    }

    /**
     * Parses a string with structured content.
     *
     * @param reader the PdfReader that has access to the PDF file
     * @throws DocumentNotSupportedException if the document cannot be processed
     * @throws IOException                   in case the reading of the file goes wrong
     * @since 5.0.5
     */
    public void extractContent(PdfReader reader, ParsedPDF parsedPDF)
            throws IOException, DocumentNotSupportedException {
        this.mParsedPDF = parsedPDF;
        PdfDictionary catalog = reader.getCatalog();
        // get the StructTreeRoot from the root object
        PdfDictionary struct = catalog.getAsDict(PdfName.STRUCTTREEROOT);
        if (struct == null || !reader.isTagged()) {
            throw new DocumentNotSupportedException("The opened PDF document is not supported " +
                    "because it is not tagged");
        } else {
            // Inspect the child or children of the StructTreeRoot
            inspectChild(struct.getDirectObject(PdfName.K), "");
        }
    }


    /**
     * Inspects a child of a structured element. This can be an array or a
     * dictionary.
     *
     * @param k         the child to inspect
     * @param tagParent the tag of the parent
     * @throws IOException
     */
    private void inspectChild(PdfObject k, String tagParent) throws IOException {
        if (k == null) {
            return;
        }
        if (k instanceof PdfArray) {
            inspectChildArray((PdfArray) k, tagParent);
        } else if (k instanceof PdfDictionary) {
            inspectChildDictionary((PdfDictionary) k, tagParent);
        }
    }

    /**
     * If the child of a structured element is an array, we need to loop over
     * the elements.
     *
     * @param k         the child array to inspect
     * @param tagParent the tag of the parent
     */
    private void inspectChildArray(PdfArray k, String tagParent) throws IOException {
        if (k == null) {
            return;
        }
        for (int i = 0; i < k.size(); i++) {
            inspectChild(k.getDirectObject(i), tagParent);
        }
    }

    /**
     * If the child of a structured element is a dictionary, we inspect the
     * child;
     *
     * @param k         the child dictionary to inspect
     * @param tagParent the tag of the parent
     */
    private void inspectChildDictionary(PdfDictionary k, String tagParent) throws IOException {
        if (k == null) {
            return;
        }
        String tag = tagParent;
        PdfName s = k.getAsName(PdfName.S);
        if (s != null) {
            tag = PdfName.decodeName(s.toString());
            tag = TagConverter.convertTag(tag);
            if (!Pattern.matches(TagConverter.MAIN_SUPPORTED_TAGS, tag)) {
                tag = tagParent;
            }
            PdfDictionary dict = k.getAsDict(PdfName.PG);
            if (dict != null) {
                if ("Figure".equals(tag)) {
                    String content = parseTag(k.getDirectObject(PdfName.K), dict, true);
                    mParsedPDF.addImage(content);
                } else {
                    String content = parseTag(k.getDirectObject(PdfName.K), dict, false);
                    if ("P".equals(tag)) {
                        mParsedPDF.addParagraph(content);
                    } else if (Pattern.matches("H[0-9]+", tag)) {
                        mParsedPDF.addHeader(content, tag.charAt(1) - CHAR_TO_INT);
                    }
                }
            }
        }
        inspectChild(k.getDirectObject(PdfName.K), tag);
    }

    protected String xmlName(PdfName name) {
        String xmlName = name.toString().replaceFirst("/", "");
        xmlName = Character.toLowerCase(xmlName.charAt(0))
                + xmlName.substring(1);
        return xmlName;
    }

    /**
     * Searches for a tag in a page.
     *
     * @param object an identifier to find the marked content
     * @param page   a page dictionary
     */
    public String parseTag(PdfObject object, PdfDictionary page, boolean image) {
        // if the identifier is a number, we can extract the content right away
        String parsed = "";
        if (object instanceof PdfNumber) {
            PdfNumber mcid = (PdfNumber) object;
            RenderFilter filter = new MarkedContentRenderFilter(mcid.intValue());
            TextExtractionStrategy strategy;
            if (image) {
                strategy = new ImageExtractionStrategy();
            } else {
                strategy = new SimpleTextExtractionStrategy();
            }
            FilteredTextRenderListener listener = new FilteredTextRenderListener(
                    strategy, filter);
            PdfContentStreamProcessor processor = new PdfContentStreamProcessor(
                    listener);
            try {
                processor.processContent(PdfReader.getPageContent(page), page
                        .getAsDict(PdfName.RESOURCES));
            } catch (IOException e) {
                Log.e("GetPageContent", "Fail to getpagecontent: " + e.getLocalizedMessage());
            }
            parsed = listener.getResultantText();
        } else if (object instanceof PdfArray) {
            // if the identifier is an array, we call the parseTag method
            // recursively
            PdfArray arr = (PdfArray) object;
            int n = arr.size();
            StringBuilder stringBuilder = new StringBuilder();
            for (int i = 0; i < n; i++) {
                stringBuilder.append(parseTag(arr.getPdfObject(i), page, image));
            }
            parsed = stringBuilder.toString();
        } else if (object instanceof PdfDictionary) {
            // if the identifier is a dictionary, we get the resources from the
            // dictionary
            PdfDictionary mcr = (PdfDictionary) object;
            parsed = parseTag(mcr.getDirectObject(PdfName.MCID), mcr
                    .getAsDict(PdfName.PG), image);
        } else {
            parsed = "";
        }
        return parsed;
    }

}

