package com.aurora.kernel;

import com.aurora.auroralib.cache.CacheResults;
import com.aurora.kernel.event.CacheFileRequest;
import com.aurora.kernel.event.CacheFileResponse;
import com.aurora.kernel.event.TranslationRequest;
import com.aurora.kernel.event.TranslationResponse;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class ProccesingCommunicatorUnitTest {

    private static Bus sBus;
    private static ProcessingCommunicator sProcessingCommunicator;

    @BeforeClass
    public static void initialize() {
        // Create bus
        sBus = new Bus(Schedulers.newThread());

        // Create communicator
        sProcessingCommunicator = new ProcessingCommunicator(sBus);
    }

    @Test
    public void ProcessingCommunicator_cacheFile_shouldCacheFileAndReturnSuccess() {
        // Create dummy arguments
        String fileRef = "dummyfileref";
        String pluginObject = "{ \"a\": \"Hello there\"}";
        String uniquePluginName = "com.aurora.dummyplugin";

        // Create observable to listen to CacheFileRequests
        Observable<CacheFileRequest> cacheFileRequestObservable = sBus.register(CacheFileRequest.class);

        // Subscribe to observable to return response
        Disposable disposable = cacheFileRequestObservable
                .subscribe(cacheFileRequest -> sBus.post(new CacheFileResponse(true)));


        int resultCode = sProcessingCommunicator.cacheFile(fileRef, pluginObject, uniquePluginName);

        Assert.assertEquals(CacheResults.CACHE_SUCCESS, resultCode);

        disposable.dispose();
    }

    @Test
    public void ProcessingCommunicator_cacheFile_shouldNotCacheFileAndReturnFail() {
        // Create dummy arguments
        String fileRef = "dummyfileref";
        String pluginObject = "{ \"a\": \"Hello there\"}";
        String uniquePluginName = "com.aurora.dummyplugin";

        // Create observable to listen to CacheFileRequests
        Observable<CacheFileRequest> cacheFileRequestObservable = sBus.register(CacheFileRequest.class);

        // Subscribe to observable to return response
        Disposable disposable = cacheFileRequestObservable
                .subscribe(cacheFileRequest -> sBus.post(new CacheFileResponse(false)));

        // Call method under test
        int resultCode = sProcessingCommunicator.cacheFile(fileRef, pluginObject, uniquePluginName);

        Assert.assertEquals(CacheResults.CACHE_FAIL, resultCode);

        disposable.dispose();
    }


    @Test
    public void ProcessingCommunicator_translate_shouldTranslateSentencesAndReturnTranslatedSentences() {
        // Create dummy arguments
        ArrayList<String> sentencesToTranslate = new ArrayList<String>(
                Arrays.asList("Hello World!", "This is a test."));
        String sourceLanguage = "en";
        String destinationLanguage = "nl";


        // Create observable to listen to TranslationRequests
        Observable<TranslationRequest> translationRequestObservable = sBus.register(TranslationRequest.class);

        // Subscribe to observable to return response
        Disposable disposable = translationRequestObservable
                .subscribe(translationRequest -> sBus.post(new TranslationResponse(
                        new String[]{"Hallo wereld!", "Dit is een test."})));

        // Call method under test
        List<String> translatedSentences = sProcessingCommunicator.translateSentences(sentencesToTranslate,
                sourceLanguage, destinationLanguage);
        Assert.assertEquals(translatedSentences.size(), 2);
        Assert.assertEquals(translatedSentences.get(0), "Hallo wereld!");
        Assert.assertEquals(translatedSentences.get(1), "Dit is een test.");
        disposable.dispose();
    }

    @Test
    public void ProcessingCommunicator_translate_shouldFailAndReturnEmptyList() {
        // Create dummy arguments
        ArrayList<String> sentencesToTranslate = new ArrayList<String>(
                Arrays.asList("Hello World!", "This is a test."));
        String sourceLanguage = "en";
        String destinationLanguage = "nl";


        // Create observable to listen to TranslationRequests
        Observable<TranslationRequest> translationRequestObservable = sBus.register(TranslationRequest.class);

        // Subscribe to observable to return response
        Disposable disposable = translationRequestObservable
                .subscribe(translationRequest -> sBus.post(new TranslationResponse(
                        new String[]{})));

        // Call method under test
        List<String> translatedSentences = sProcessingCommunicator.translateSentences(sentencesToTranslate,
                sourceLanguage, destinationLanguage);
        Assert.assertEquals(translatedSentences.size(), 0);
        disposable.dispose();

    }
}
