package com.aurora.kernel;

import android.util.Log;

import com.aurora.auroralib.ExtractedText;
import com.aurora.internalservice.internalnlp.InternalNLP;
import com.aurora.internalservice.internalprocessor.FileTypeNotSupportedException;
import com.aurora.internalservice.internalprocessor.InternalTextProcessor;
import com.aurora.kernel.event.InternalProcessorRequest;
import com.aurora.kernel.event.InternalProcessorResponse;
import com.aurora.plugin.InternalServices;

import java.io.InputStream;
import java.util.List;

import io.reactivex.Observable;

/**
 * Communicator that communicates with internal services offered to plugin processors
 */
public class PluginInternalServiceCommunicator extends Communicator {

    /**
     * Tag for logging purposes
     */
    private static final String CLASS_TAG = "PIServiceCommunicator";

    /**
     * internal text processor
     */
    private InternalTextProcessor mInternalTextProcessor;

    /** CoreNLP pipeline */
    private InternalNLP mNLPPipeline;

    /**
     * Observable keeping track of internal processor requests
     */
    private Observable<InternalProcessorRequest> mInternalProcessorRequestObservable;

    /**
     * Creates a PluginInternalServiceCommunicator. There should be only one instance at a time
     *
     * @param mBus      a reference to the unique bus instance that all communicators should be using for
     *                  communicating events
     * @param processor a reference to the InternalTextProcessor
     */
    public PluginInternalServiceCommunicator(Bus mBus, InternalTextProcessor processor) {
        super(mBus);
        mInternalTextProcessor = processor;

        mInternalProcessorRequestObservable = mBus.register(InternalProcessorRequest.class);
        mInternalProcessorRequestObservable.subscribe((InternalProcessorRequest request) ->
                processFileWithInternalProcessor(request.getFileRef(), request.getFileType(), request.getFile(),
                        request.getInternalServices()));
    }

    /**
     * Helper method to process a file with the internal processor when a request comes in
     *
     * @param fileRef          a reference to the file that should be processed
     * @param type             the file type
     * @param file             the file input stream
     * @param internalServices the set of internal services that should be run on the file
     */
    private void processFileWithInternalProcessor(String fileRef, String type, InputStream file,
                                                  List<InternalServices> internalServices) {
        ExtractedText extractedText = null;

        // Perform internal services that are in the given set
        if (internalServices.contains(InternalServices.TEXT_EXTRACTION)) {
            // Call internal text processor
            try {
                boolean extractImages =
                        internalServices.contains(InternalServices.IMAGE_EXTRACTION);

                extractedText = mInternalTextProcessor.processFile(file, fileRef, type, extractImages);

                Log.d( CLASS_TAG,
                        "Service completed: " + InternalServices.TEXT_EXTRACTION.name());
                if(extractImages) {
                    Log.d(CLASS_TAG,
                            "Service completed: " + InternalServices.IMAGE_EXTRACTION.name());
                }
            } catch (FileTypeNotSupportedException e) {
                Log.e(CLASS_TAG, "File type is not supported!", e);
            }
        }

        // If extractedText is null for some reason: return default extracted text
        if (extractedText == null) {
            extractedText = new ExtractedText("", null);
        }

        // Add all NLP steps to the pipeline
        for (InternalServices internalService: internalServices) {

            if(internalService.name().startsWith("NLP_")) {
                if(mNLPPipeline == null) {
                    mNLPPipeline = new InternalNLP();
                }

                mNLPPipeline.addAnnotator(internalService);
            }

        }

        if(mNLPPipeline != null) {
             mNLPPipeline.annotate(extractedText);
            Log.d(CLASS_TAG, "Service completed: " + "NLP ANNOTATION");
        }


        // Create response
        InternalProcessorResponse response = new InternalProcessorResponse(extractedText);

        // Post response
        mBus.post(response);
    }

}
