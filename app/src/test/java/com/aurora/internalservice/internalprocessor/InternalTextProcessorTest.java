package com.aurora.internalservice.internalprocessor;

import com.aurora.auroralib.ExtractedText;

import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import static org.junit.Assert.*;


public class InternalTextProcessorTest {
    private static InternalTextProcessor mInternalTextProcessor = new InternalTextProcessor();

    // Test if an exception is thrown when a wrong file type is passed
    @Test(expected = FileTypeNotSupportedException.class)
    public void processFile_shouldThrowErrorUnsupportedExtension() throws FileTypeNotSupportedException {
        String fileRef = "dummy/file/ref.pdf";

        mInternalTextProcessor.processFile(null, fileRef, "jpg");
    }

    // Test if, when a 'txt'-file is passed, text is extracted
    @Test
    public void processFile_shouldReturnExtractedTextTXT() throws FileTypeNotSupportedException {
        String fileRef = "src/test/res/Pasta.txt";
        File file = new File(fileRef);
        try {
            InputStream inputStream = new FileInputStream(file);
            ExtractedText extractedText = mInternalTextProcessor.processFile(inputStream, fileRef, "txt");
            assertNotNull("The extraction of the 'txt'-file has failed" , extractedText);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    // Test if, when a 'docx'-file is passed, text is extracted
    @Test
    public void processFile_shouldReturnExtractedTextDOCX() throws FileTypeNotSupportedException {
        /* Set system properties for DOCX */
        System.setProperty("org.apache.poi.javax.xml.stream.XMLInputFactory",
                "com.fasterxml.aalto.stax.InputFactoryImpl");
        System.setProperty("org.apache.poi.javax.xml.stream.XMLOutputFactory",
                "com.fasterxml.aalto.stax.OutputFactoryImpl");
        System.setProperty("org.apache.poi.javax.xml.stream.XMLEventFactory",
                "com.fasterxml.aalto.stax.EventFactoryImpl");

        String fileRef = "src/test/res/Pasta.docx";
        File file = new File(fileRef);
        try {
            InputStream inputStream = new FileInputStream(file);
            ExtractedText extractedText = mInternalTextProcessor.processFile(inputStream, fileRef, "docx");
            assertNotNull("The extraction of the 'docx'-file has failed" , extractedText);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

//    // Test if, when a 'pdf'-file is passed, text is extracted
//    @Test
//    public void processFile_shouldReturnExtractedTextPDF() throws FileTypeNotSupportedException {
//        String fileRef = "src/test/res/Pasta.pdf";
//        File file = new File(fileRef);
//        try {
//            InputStream inputStream = new FileInputStream(file);
//            ExtractedText extractedText = mInternalTextProcessor.processFile(inputStream, fileRef, "pdf");
//            assertNotNull("The extraction of the 'pdf'-file has failed" , extractedText);
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
//    }
}