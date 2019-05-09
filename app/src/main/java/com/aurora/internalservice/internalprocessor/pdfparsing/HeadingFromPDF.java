package com.aurora.internalservice.internalprocessor.pdfparsing;

public class HeadingFromPDF implements PDFStructureElement {
    public static final String TYPE = "H";
    private String mHeader;
    private int mLevel;

    public HeadingFromPDF(String text, int level) {
        mHeader = text;
        mLevel = level;
    }

    @Override
    public String getType() {
        return TYPE;
    }

    @Override
    public String getContent() {
        return mHeader;
    }

    @Override
    public int getLevel() {
        return mLevel;
    }
}

