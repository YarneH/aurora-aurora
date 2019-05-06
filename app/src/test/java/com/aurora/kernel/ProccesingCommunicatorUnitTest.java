package com.aurora.kernel;

import com.aurora.auroralib.cache.CacheResults;
import com.aurora.kernel.event.CacheFileRequest;
import com.aurora.kernel.event.CacheFileResponse;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

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
}
