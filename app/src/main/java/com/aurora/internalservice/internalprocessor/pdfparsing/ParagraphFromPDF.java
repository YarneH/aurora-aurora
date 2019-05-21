package com.aurora.internalservice.internalprocessor.pdfparsing;

public class ParagraphFromPDF implements PDFStructureElement {
    public static final String TYPE = "P";
    private String mParagraph;
    ParagraphFromPDF(String text) {
        mParagraph = text;
    }

    @Override
    public String getType() {
        return TYPE;
    }

    @Override
    public String getContent() {
        return mParagraph;
    }

    @Override
    public int getLevel() {
        return 0;
    }
}
