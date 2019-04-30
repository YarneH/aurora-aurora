package com.aurora.internalservice.internalprocessor.pdfparsing;

public interface PDFStructureElement {
    String getType();
    String getContent();
    int getLevel();
}
