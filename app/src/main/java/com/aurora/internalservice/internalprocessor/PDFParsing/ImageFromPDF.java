package com.aurora.internalservice.internalprocessor.PDFParsing;

public class ImageFromPDF implements PDFStructureElement {
    private String mImage;
    public final static String TYPE = "Image";
    public ImageFromPDF(String image) {
        mImage = image;
    }

    @Override
    public String getType() {
        return TYPE;
    }

    @Override
    public String getContent() {
        return mImage;
    }

    @Override
    public int getLevel() {
        return 0;
    }
}
