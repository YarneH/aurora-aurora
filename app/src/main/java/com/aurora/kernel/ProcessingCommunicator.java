package com.aurora.kernel;

import android.support.annotation.NonNull;
import android.util.Log;

import com.aurora.auroralib.cache.CacheResults;
import com.aurora.auroralib.translation.TranslationErrorCodes;
import com.aurora.auroralib.translation.TranslationResult;
import com.aurora.kernel.event.CacheFileRequest;
import com.aurora.kernel.event.CacheFileResponse;
import com.aurora.kernel.event.TranslationRequest;
import com.aurora.kernel.event.TranslationResponse;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import io.reactivex.Observable;

/**
 * Communicator that communicates with Plugin processors
 */
public class ProcessingCommunicator extends Communicator {
    private static final String LOG_TAG = "ProcessingCommunicator";

    private Observable<CacheFileResponse> mCacheFileResponseObservable;
    private Observable<TranslationResponse> mTranslationResponseObservable;

    /**
     * Creates a ProcessingCommunicator. There should be only one instance at a time
     *
     * @param mBus a reference to the unique bus instance that all communicators should use to communicate events
     */
    public ProcessingCommunicator(Bus mBus) {
        super(mBus);

        // Subscribe to observable
        mCacheFileResponseObservable = mBus.register(CacheFileResponse.class);
        mTranslationResponseObservable = mBus.register(TranslationResponse.class);
    }

    /**
     * Caches a json representation processed by a plugin
     *
     * @param fileRef          a reference to the original file
     * @param pluginObject     a json representation of the object that needs to be cached
     * @param uniquePluginName the name of the plugin that the file was processed with
     * @return a status code indicating if the cache operation was successful (0) or not (-1)
     */
    public int cacheFile(@NonNull String fileRef, @NonNull String pluginObject, @NonNull String uniquePluginName) {
        // response contains boolean, which is converted to a status code which is then synchronously returned
        AtomicBoolean isSet = new AtomicBoolean(false);
        AtomicInteger returnCode = new AtomicInteger(CacheResults.CACHE_FAIL);

        mCacheFileResponseObservable
                .take(1)
                .map(CacheFileResponse::isSuccessful)
                .map((Boolean successful) -> {
                    if (successful) {
                        return CacheResults.CACHE_SUCCESS;
                    } else {
                        return CacheResults.CACHE_FAIL;
                    }
                })
                .subscribe((Integer cacheResult) -> {
                    synchronized (this) {
                        returnCode.set(cacheResult);
                        isSet.set(true);
                        notifyAll();
                    }
                });

        // Create request to cache the file

        CacheFileRequest cacheFileRequest = new CacheFileRequest(fileRef, pluginObject, uniquePluginName);

        // Post on the bus
        mBus.post(cacheFileRequest);

        synchronized (this) {
            while(!isSet.get()) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    Log.e(LOG_TAG, "Interrupted in ProcessingCommunicator", e);

                    Thread.currentThread().interrupt();
                }
            }

            return returnCode.get();
        }
    }

    /**
     * translate sentences sent by a plugin
     *
     * @param sentences             the list of strings to be translated
     * @param sourceLanguage        the language of the input sentences in ISO code
     * @param destinationLanguage   the desired language of the translations in ISO format
     * @return the list of translated sentences
     */
    public List<String> translateSentences(@NonNull List<String> sentences,
                                           String sourceLanguage,
                                           @NonNull String destinationLanguage) {
        // response contains boolean, which is converted to a status code which is then synchronously returned
        AtomicBoolean isSet = new AtomicBoolean(false);
        final AtomicInteger errorCode = new AtomicInteger(TranslationErrorCodes.TRANSLATION_FAIL);
        final AtomicReference<String []> translatedSentences = new AtomicReference<>();

        mTranslationResponseObservable.subscribe((TranslationResponse response) -> {
            synchronized (this) {
                        isSet.set(true);
                        String errorMessage = response.getErrorMessage();
                        if (errorMessage == null){
                            errorCode.set(TranslationErrorCodes.TRANSLATION_SUCCESS);
                            translatedSentences.set(response.getTranslatedSentences());
                        } else{
                            Log.e(LOG_TAG, errorMessage);
                        }
                        notifyAll();
                    }
                });

        // Create request to cache the file

        TranslationRequest translationRequest = new TranslationRequest(
                sentences.toArray(new String[0]), sourceLanguage, destinationLanguage);

        // Post on the bus
        mBus.post(translationRequest);

        synchronized (this) {
            while(!isSet.get()) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    Log.e(LOG_TAG, "Interrupted in ProcessingCommunicator", e);

                    Thread.currentThread().interrupt();
                }
            }

            return new ArrayList<>(Arrays.asList(translatedSentences.get()));
            //return new TranslationResult(errorCode.get(), translatedSentences.get());
        }
    }

}
