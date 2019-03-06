package com.aurora.processingservice;

import com.aurora.internalservice.internalprocessor.ExtractedText;
import com.aurora.kernel.ProcessingCommunicator;
import com.aurora.plugin.ProcessedText;

import io.reactivex.Observable;

/**
 * Class that maintains some information about the Plugin processor.
 * Instantiated classes will define the processing logic for files.
 */
public abstract class PluginProcessor {

    private ProcessingCommunicator mProcessingCommunicator;

    public PluginProcessor(ProcessingCommunicator processingCommunicator) {
        this.mProcessingCommunicator = processingCommunicator;
    }

    /**
     * This method will be called by the ProcessingCommunicator when a Environment asks to process
     * a file
     *
     * @param fileRef a reference to where the file can be found
     * @return object that contains all the information the Environment would need to create an
     * enhanced representation
     */
    public abstract ProcessedText processFileWithPluginProcessor(String fileRef);

    /**
     * Use the the internal processing of Aurora to extract text, upon returning calls the
     * resultProcessFileWithAuroraProcessor method
     *
     * @param fileRef a reference to where the file can be found
     */
    protected final void processFileWithAuroraProcessor(String fileRef) {
        Observable<ExtractedText> extractedTextObservable =
                mProcessingCommunicator.processFileWithAuroraProcessor(fileRef);


        extractedTextObservable.subscribe(this::resultProcessFileWithAuroraProcessor);
    }

    /**
     * This method is automatically called upon receiving a response from
     * processFileWithAuroraProcessor
     *
     * @param extractedText the object returned by the internal processor
     */
    protected abstract void resultProcessFileWithAuroraProcessor(ExtractedText extractedText);

}
