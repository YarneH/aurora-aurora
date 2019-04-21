package com.aurora.internalservice.internalprocessor.pdfparsing;

public interface PDFStructureElement {
    public String getType();
    public String getContent();
    public int getLevel();
}
