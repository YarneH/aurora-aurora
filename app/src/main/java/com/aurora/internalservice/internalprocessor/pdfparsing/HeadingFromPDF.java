package com.aurora.internalservice.internalprocessor.pdfparsing;

public class HeadingFromPDF implements PDFStructureElement {
    public static final String TYPE = "H";
    private String mHeader;
    private int level;
    public HeadingFromPDF(String text, int level) {
        mHeader = text;
    }

    @Override
    public String getType() {
        return TYPE;
    }

    @Override
    public String getContent() {
        return mHeader;
    }

    public int getLevel(){
        return level;
}
}

