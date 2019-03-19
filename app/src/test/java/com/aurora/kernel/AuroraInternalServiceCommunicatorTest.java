package com.aurora.kernel;

import com.aurora.internalservice.internalcache.CachedProcessedFile;
import com.aurora.internalservice.internalcache.InternalCache;
import com.aurora.kernel.event.CacheFileRequest;
import com.aurora.kernel.event.CacheFileResponse;
import com.aurora.kernel.event.QueryCacheRequest;
import com.aurora.kernel.event.QueryCacheResponse;
import com.aurora.plugin.ProcessedText;

import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.observers.TestObserver;

public class AuroraInternalServiceCommunicatorTest {
    private static Bus mBus;
    private static InternalCache mInternalCache;
    private static AuroraInternalServiceCommunicator mAuroraInternalServiceCommunicator;

    private static List<CachedProcessedFile> dummyList = new ArrayList<>();
    private static CachedProcessedFile dummyCachedFile = new CachedProcessedFile();

    @BeforeClass
    public static void initialize() {
        // Create bus
        mBus = new Bus();

        // Create internal cache
        mInternalCache = new DummyInternalCache();

        // Create Communicator
        mAuroraInternalServiceCommunicator = new AuroraInternalServiceCommunicator(mBus, mInternalCache);
    }

    @Test
    public void AuroraInternalServiceCommunicator_shouldCacheFileOnRequest() {
        // Subscribe to CacheFileResponse
        Observable<CacheFileResponse> observable = mBus.register(CacheFileResponse.class);

        // Create test observer for response
        TestObserver<Boolean> testObserver = new TestObserver<>();

        // Subscribe to observable
        observable.map(CacheFileResponse::isSuccessful).subscribe(testObserver);

        // Create cache file request and post on bus
        String fileRef = "Dummy/file/path.pdf";
        ProcessedText text = new DummyProcessedText("Title", Arrays.asList("Hello", "There"));
        String uniquePluginName = "DummyPlugin";
        CacheFileRequest request = new CacheFileRequest(fileRef, text, uniquePluginName);
        mBus.post(request);

        // Check if cache returned true
        testObserver.assertSubscribed();
        testObserver.assertValue(true);
    }

    @Test
    public void AuroraInternalServiceCommunicator_shouldReturnAllCachedFilesOnEmptyRequest() {
        // Subscribe to QueryCacheResponse
        Observable<QueryCacheResponse> observable = mBus.register(QueryCacheResponse.class);

        // Create test observer for response
        TestObserver<List<CachedProcessedFile>> testObserver = new TestObserver<>();

        // Subscribe to observable
        observable.map(QueryCacheResponse::getResults).subscribe(testObserver);

        // Create query request and post on the bus
        QueryCacheRequest queryCacheRequest = new QueryCacheRequest();
        mBus.post(queryCacheRequest);

        // Check if cache returned list
        testObserver.assertSubscribed();
        testObserver.assertValue(dummyList);
    }

    @Test
    public void AuroraInternalServiceCommunicator_shouldReturnSpecificFileOnRequest() {
        // Subscribe to QueryCacheResponse
        Observable<QueryCacheResponse> observable = mBus.register(QueryCacheResponse.class);

        // Create test observer for response
        TestObserver<CachedProcessedFile> testObserver = new TestObserver<>();

        // Subscribe to observable
        observable.map(queryCacheResponse -> queryCacheResponse.getResults().get(0)).subscribe(testObserver);

        // Create query request and post on the bus
        String fileRef = "dummy/file/ref.pdf";
        String uniquePluginName = "DummyPlugin";
        QueryCacheRequest queryCacheRequest = new QueryCacheRequest(fileRef, uniquePluginName);
        mBus.post(queryCacheRequest);

        // Check if cache returned specific plugin
        testObserver.assertSubscribed();
        testObserver.assertValue(dummyCachedFile);
    }

    /**
     * Dummy class with stub implementations for the cache
     */
    private static class DummyInternalCache extends InternalCache {
        @Override
        public boolean cacheFile(String fileRef, ProcessedText text, String uniquePluginName) {
            // Just return true
            return true;
        }

        @Override
        public CachedProcessedFile checkCacheForProcessedFile(String fileRef, String uniquePluginName) {
            return dummyCachedFile;
        }

        @Override
        public List<CachedProcessedFile> getFullCache() {
            return dummyList;
        }
    }

    /**
     * Dummy implementation for testing purposes
     */
    private class DummyProcessedText extends ProcessedText {

        public DummyProcessedText(String title, List<String> paragraphs) {
            super(title, paragraphs);
        }
    }
}
