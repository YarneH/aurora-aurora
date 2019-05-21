package com.aurora.internalservice.internalprocessor.pdfparsing;

public class ImageFromPDF implements PDFStructureElement {
    private String mImage;
    public static final String TYPE = "ExtractedImage";
    ImageFromPDF(String image) {
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
