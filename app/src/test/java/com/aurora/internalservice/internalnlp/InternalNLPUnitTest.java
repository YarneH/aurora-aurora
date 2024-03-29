package com.aurora.internalservice.internalnlp;

import com.aurora.auroralib.ExtractedText;
import com.aurora.auroralib.Section;
import com.aurora.internalservice.internalprocessor.DocumentNotSupportedException;
import com.aurora.internalservice.internalprocessor.FileTypeNotSupportedException;
import com.aurora.internalservice.internalprocessor.InternalTextProcessor;
import com.aurora.plugin.InternalServices;

import org.apache.commons.lang3.NotImplementedException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class InternalNLPUnitTest {

    private static InternalNLP mInternalNLP;
    private static InternalTextProcessor mInternalTextProcessor = new InternalTextProcessor();

    @Before
    public void initialize() {
        mInternalNLP = new InternalNLP();
    }

    @Test
    public void InternalNLP_annotate_doesAnnotate() {
        // Load a text file in order to have an ExtractedText object
        ExtractedText extractedText = null;

        String fileUri = "dummyUri";
        String fileRef = "src/test/res/Pasta.txt";
        File file = new File(fileRef);
        try {
            InputStream inputStream = new FileInputStream(file);
            extractedText = mInternalTextProcessor.processFile(inputStream, fileUri, fileRef
                    , "txt", false);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (FileTypeNotSupportedException e) {
            e.printStackTrace();
        } catch (DocumentNotSupportedException e) {
            e.printStackTrace();
        }

        // Annotate
        mInternalNLP.annotate(extractedText);

        if(!extractedText.getTitle().isEmpty()) {
            Assert.assertNotNull(extractedText.getTitleAnnotation());
        }

        for (Section section: extractedText.getSections()) {
            if(!section.getBody().isEmpty()) {
                Assert.assertNotNull(section.getBodyAnnotation());
            }
            if(!section.getTitle().isEmpty()) {
                Assert.assertNotNull(section.getTitleAnnotation());
            }
        }
    }

    @Test
    public void InternalNLP_addAnnotator_NoExceptionsAreThrown(){

        boolean thrown = false;
        try {
            mInternalNLP.addAnnotator(InternalServices.NLP_TOKENIZE);

        } catch (Exception e) {
            thrown = true;
        }

        assert (!thrown);
    }

    @Test
    public void InternalNLP_addUnsupportedAnnotator_ExceptionIsThrown(){

        boolean thrown = false;
        try {
            mInternalNLP.addAnnotator(InternalServices.IMAGE_EXTRACTION);

        } catch (NotImplementedException e) {
            thrown = true;
        }

        assert (thrown);
    }

    @Test
    public void InternalNLP_addUnsatisfiedAnnotator_ExceptionIsThrown(){
        boolean thrown = false;
        try {
            mInternalNLP.addAnnotator(InternalServices.NLP_SSPLIT);

        } catch (UnsupportedOperationException e) {
            thrown = true;
        }

        assert (thrown);
    }
}
