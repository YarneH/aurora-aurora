package com.aurora.kernel;

import android.support.annotation.NonNull;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.aurora.auroralib.ExtractedText;
import com.aurora.internalservice.internalnlp.InternalNLP;
import com.aurora.internalservice.internalprocessor.FileTypeNotSupportedException;
import com.aurora.internalservice.internalprocessor.InternalTextProcessor;
import com.aurora.internalservice.internaltranslation.Translator;
import com.aurora.kernel.event.InternalProcessorRequest;
import com.aurora.kernel.event.InternalProcessorResponse;
import com.aurora.kernel.event.TranslationRequest;
import com.aurora.kernel.event.TranslationResponse;
import com.aurora.plugin.InternalServices;

import org.apache.commons.lang3.NotImplementedException;

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

    /**
     * InternalNLP object, loads some annotators statically so needs to keep living for
     * performance
     */
    private InternalNLP mInternalNLP;

    /**
     * A reference to the translator for translating requests
     */
    private Translator mTranslator;
    /**
     * Observable keeping track of internal processor requests
     */
    private Observable<InternalProcessorRequest> mInternalProcessorRequestObservable;

    /**
     * Observable keeping track of translation requests
     */
    private Observable<TranslationRequest> mTranslationRequestObservable;

    /**
     * Creates a PluginInternalServiceCommunicator. There should be only one instance at a time
     *
     * @param mBus      a reference to the unique bus instance that all communicators should be using for
     *                  communicating events
     * @param processor a reference to the InternalTextProcessor
     * @param translator a reference to the internal translator
     */
    public PluginInternalServiceCommunicator(@NonNull final Bus mBus, @NonNull final InternalTextProcessor processor,
                                             @NonNull final Translator translator) {
        super(mBus);
        mInternalTextProcessor = processor;
        mTranslator = translator;


        mInternalProcessorRequestObservable = mBus.register(InternalProcessorRequest.class);
        mInternalProcessorRequestObservable.subscribe((InternalProcessorRequest request) ->
                processFileWithInternalProcessor(request.getFileRef(), request.getFileType(), request.getFile(),
                        request.getInternalServices()));

        mTranslationRequestObservable = mBus.register(TranslationRequest.class);
        mTranslationRequestObservable.subscribe((TranslationRequest request) -> {
            TranslationResponse response = mTranslator.translate(request);
            if (response != null) {
                mBus.post(response);
            } else {
                mBus.post(new TranslationResponse("Something went wrong in the translation"));
            }
        });

    }


    /**
     * Helper method to process a file with the internal processor when a request comes in
     *
     * @param fileRef          a reference to the file that should be processed
     * @param type             the file type
     * @param file             the file input stream
     * @param internalServices the set of internal services that should be run on the file
     */
    private void processFileWithInternalProcessor(@NonNull final String fileRef, @NonNull String type,
                                                  final InputStream file,
                                                  @NonNull final List<InternalServices> internalServices) {

        // STEP ONE
        ExtractedText extractedText = doTextAndImageExtractionTasks(internalServices, file,
                fileRef, type);

        // If extractedText is null for some reason: return default extracted text
        if (extractedText == null) {
            extractedText = new ExtractedText("", null);
        }

        // STEP TWO
        doNLPTask(extractedText, internalServices);

        // Create response
        InternalProcessorResponse response = new InternalProcessorResponse(extractedText);

        // Post response
        mBus.post(response);
    }

    /**
     * Private method that does the ImageExtraction and TextExtraction tasks if requested
     *
     * @param internalServices the set of internal services that should be run on the file
     * @param file             the file inputstream
     * @param fileRef          a reference to the file that should be processed
     * @param type             the file type (extension)
     * @return ExtractedText object
     */
    private ExtractedText doTextAndImageExtractionTasks(List<InternalServices> internalServices,
                                                        InputStream file, String fileRef, String type) {
        // Perform internal services that are in the given set

        ExtractedText extractedText = null;

        if (internalServices.contains(InternalServices.TEXT_EXTRACTION)) {
            // Call internal text processor
            try {
                boolean extractImages =
                        internalServices.contains(InternalServices.IMAGE_EXTRACTION);

                extractedText = mInternalTextProcessor.processFile(file, fileRef, type,
                        extractImages);

                Log.d(CLASS_TAG,
                        "Service completed: " + InternalServices.TEXT_EXTRACTION.name());
                if (extractImages) {
                    Log.d(CLASS_TAG,
                            "Service completed: " + InternalServices.IMAGE_EXTRACTION.name());
                }

            } catch (FileTypeNotSupportedException e) {
                Log.e(CLASS_TAG, "File type is not supported!", e);
            }
        }
        return extractedText;
    }

    /**
     * Private method that does the InternalNLP annotation if requested
     *
     * @param extractedText    extractedText object that should be annotated
     * @param internalServices the services to determine if the NLP service is requested
     */
    private void doNLPTask(ExtractedText extractedText, List<InternalServices> internalServices) {
        boolean doNLP = false;

        // Add all NLP steps to the pipeline
        for (InternalServices internalService : internalServices) {

            if (internalService.name().startsWith("NLP_")) {
                if (mInternalNLP == null) {
                    mInternalNLP = new InternalNLP();
                }

                try {
                    mInternalNLP.addAnnotator(internalService);
                    doNLP = true;
                } catch (NotImplementedException e) {
                    Log.e(CLASS_TAG, "Something went wrong when building the NLP pipeline", e);
                }
            }
        }

        if (doNLP) {
            mInternalNLP.annotate(extractedText);
            Log.d(CLASS_TAG, "Service completed: " + "NLP ANNOTATION");
        }
        mInternalNLP = null;
    }

}
