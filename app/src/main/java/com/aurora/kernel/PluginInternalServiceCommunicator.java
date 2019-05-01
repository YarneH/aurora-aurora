package com.aurora.kernel;

import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
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

    /** InternalNLP object, loads some annotators statically so needs to keep living for
     * performance
     */
    private InternalNLP mInternalNLP;


    /**
     * A queue to post http requests to
     */
    private RequestQueue mRequestQueue;
    /**
     * Observable keeping track of internal processor requests
     */
    private Observable<InternalProcessorRequest> mInternalProcessorRequestObservable;

    /**
     * Observable keeping track of internal processor requests
     */
    private Observable<TranslationRequest> mTranslationRequestObservable;

    /**
     * Creates a PluginInternalServiceCommunicator. There should be only one instance at a time
     *
     * @param mBus      a reference to the unique bus instance that all communicators should be using for
     *                  communicating events
     * @param processor a reference to the InternalTextProcessor
     */
    public PluginInternalServiceCommunicator(Bus mBus, InternalTextProcessor processor,
                                             RequestQueue requestQueue) {
        super(mBus);
        mInternalTextProcessor = processor;
        mRequestQueue = requestQueue;


        mInternalProcessorRequestObservable = mBus.register(InternalProcessorRequest.class);
        mInternalProcessorRequestObservable.subscribe((InternalProcessorRequest request) ->
                processFileWithInternalProcessor(request.getFileRef(), request.getFileType(), request.getFile(),
                        request.getInternalServices()));

        mTranslationRequestObservable = mBus.register(TranslationRequest.class);
        mTranslationRequestObservable.subscribe((TranslationRequest request) ->
            translate(request.getSentencesToTranslate(), request.getSourceLanguage(), request.getTargetLanguage()));


        // test code for translation
        String[] sentences = {"hello my name is Luca", "Does the translating work?"};
        mBus.post(new TranslationRequest(sentences, "en", "nl"));

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
     * @param file              the file inputstream
     * @param fileRef           a reference to the file that should be processed
     * @param type              the file type (extension)
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
        return extractedText;
    }

    /**
     * Private method that does the InternalNLP annotation if requested
     *
     * @param extractedText     extractedText object that should be annotated
     * @param internalServices  the services to determine if the NLP service is requested
     */
    private void doNLPTask(ExtractedText extractedText, List<InternalServices> internalServices) {
        boolean doNLP = false;

        // Add all NLP steps to the pipeline
        for (InternalServices internalService: internalServices) {

            if(internalService.name().startsWith("NLP_")) {
                if(mInternalNLP == null) {
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

        if(doNLP) {
            mInternalNLP.annotate(extractedText);
            Log.d(CLASS_TAG, "Service completed: " + "NLP ANNOTATION");
        }
    }

    /**
     * private helper method for when a {@link TranslationRequest} comes in. It calls
     * {@link Translator#makeUrl(String[], String, String)} to get a url and posts this to the {@link #mRequestQueue}
     *
     * @param sentencesToTranslate the sentences to translate
     * @param sourceLanguage       language to translate from
     * @param targetLanguage       language to translate to
     */
    private void translate(String[] sentencesToTranslate, String sourceLanguage, String targetLanguage) {
        try {
            String url = Translator.makeUrl(sentencesToTranslate, sourceLanguage, targetLanguage);
            Log.d("TRANSLATE", url);
            // Request a json response from the provided URL.
            // TODO needs to be checked after acquiring key
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url,
                    null, this::postTranslationResponseEvent, this::postTranslationResponseEvent);
            jsonObjectRequest.setTag("TRANSLATOR");

            mRequestQueue.add(jsonObjectRequest);
            Log.d("TRANSLATE", "request added");
        } catch (IOException e) {
            Log.e("TRANSLATION", "Translation failed", e);
            postTranslationResponseEvent(e);
        }
    }

    /**
     * Posts a {@link TranslationResponse} event getting the translated data from the argument
     *
     * @param response the respons from the {@link RequestQueue} to the Google API
     */
    private void postTranslationResponseEvent(JSONObject response) {
        try {
            mBus.post(Translator.getTranslationResponse(response));

        } catch (JSONException e) {
            Log.e("JSON", "getting from json failed", e);
            postTranslationResponseEvent(e);
        }


    }

    /**
     * Posts a {@link TranslationResponse} that signifies the transation failed
     *
     * @param error the reason why the translation failed
     */
    private void postTranslationResponseEvent(Exception error) {
        TranslationResponse errorResponse = new TranslationResponse(error.getMessage());
        mBus.post(errorResponse);
    }

}
