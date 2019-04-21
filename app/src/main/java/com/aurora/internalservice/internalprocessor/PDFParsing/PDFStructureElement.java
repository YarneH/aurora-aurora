package com.aurora.internalservice.internalprocessor.PDFParsing;

public interface PDFStructureElement {
    public String getType();
    public String getContent();
    public int getLevel();
}
