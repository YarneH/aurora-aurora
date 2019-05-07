package com.aurora.kernel;

import android.content.Context;
import android.support.annotation.NonNull;

import com.aurora.auroralib.PluginObject;
import com.aurora.internalservice.internalcache.CachedFileInfo;
import com.aurora.internalservice.internalcache.CachedProcessedFile;
import com.aurora.internalservice.internalcache.InternalCache;
import com.aurora.kernel.event.CacheFileRequest;
import com.aurora.kernel.event.CacheFileResponse;
import com.aurora.kernel.event.QueryCacheRequest;
import com.aurora.kernel.event.QueryCacheResponse;
import com.aurora.kernel.event.RemoveFromCacheRequest;
import com.aurora.kernel.event.RemoveFromCacheResponse;
import com.aurora.kernel.event.RetrieveFileFromCacheRequest;
import com.aurora.kernel.event.RetrieveFileFromCacheResponse;
import com.aurora.util.MockContext;

import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.observers.TestObserver;
import io.reactivex.schedulers.Schedulers;

public class AuroraInternalServiceCommunicatorUnitTest {
    private static Bus mBus;
    private static InternalCache mInternalCache;
    private static AuroraInternalServiceCommunicator mAuroraInternalServiceCommunicator;

    private static List<CachedFileInfo> dummyList = new ArrayList<>();
    private static String fileRef = "CachedFile.pdf";
    private static String pluginName = "DummyPlugin";
    private static CachedFileInfo dummyCachedFileInfo = new CachedFileInfo(fileRef, pluginName);
    private static CachedProcessedFile dummyCachedFile = new CachedProcessedFile("{}", fileRef, pluginName);

    @BeforeClass
    public static void initialize() {
        // Create bus
        mBus = new Bus(Schedulers.trampoline());

        // Create internal cache
        mInternalCache = new DummyInternalCache(new MockContext());

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
        String dummyPluginObject = new DummyPluginObject().toJSON();
        CacheFileRequest request = new CacheFileRequest(fileRef, dummyPluginObject, pluginName);
        mBus.post(request);

        // Check if cache returned true
        testObserver.assertSubscribed();
        testObserver.assertValue(true);
        testObserver.dispose();
    }

    @Test
    public void AuroraInternalServiceCommunicator_shouldQueryAllCachedFilesOnEmptyRequest() {
        // Subscribe to QueryCacheResponse
        Observable<QueryCacheResponse> observable = mBus.register(QueryCacheResponse.class);

        // Create test observer for response
        TestObserver<List<CachedFileInfo>> testObserver = new TestObserver<>();

        // Subscribe to observable
        observable.map(QueryCacheResponse::getResults).subscribe(testObserver);

        // Create query request and post on the bus
        QueryCacheRequest queryCacheRequest = new QueryCacheRequest();
        mBus.post(queryCacheRequest);

        // Check if cache returned list
        testObserver.assertSubscribed();
        testObserver.assertValue(dummyList);
        testObserver.dispose();
    }

    @Test
    public void AuroraInternalServiceCommunicator_shouldQuerySpecificFileOnRequest() {
        // Subscribe to QueryCacheResponse
        Observable<QueryCacheResponse> observable = mBus.register(QueryCacheResponse.class);

        // Create test observer for response
        TestObserver<CachedFileInfo> testObserver = new TestObserver<>();

        // Subscribe to observable
        observable.map(queryCacheResponse -> queryCacheResponse.getResults().get(0)).subscribe(testObserver);

        // Create query request and post on the bus-
        QueryCacheRequest queryCacheRequest = new QueryCacheRequest(fileRef, pluginName);
        mBus.post(queryCacheRequest);

        // Check if cache returned specific plugin
        testObserver.assertSubscribed();
        testObserver.assertValue(dummyCachedFileInfo);
        testObserver.dispose();
    }

    @Test
    public void AuroraInternalServiceCommunicator_shouldRetrieveSpecificFileOnRequest() {
        // Subscribe to RetrieveFileFromCacheResponse
        Observable<RetrieveFileFromCacheResponse> observable = mBus.register(RetrieveFileFromCacheResponse.class);

        // Create test observer for response
        TestObserver<CachedProcessedFile> testObserver = new TestObserver<>();

        // Subscribe to observable
        observable.map(RetrieveFileFromCacheResponse::getProcessedFile).subscribe(testObserver);

        // Create retrieve request and post on bus
        String fileRef = "dummy/file/ref.pdf";
        String uniquePluginName = "DummyPlugin";
        RetrieveFileFromCacheRequest request = new RetrieveFileFromCacheRequest(fileRef, uniquePluginName);
        mBus.post(request);

        // Check if cache retrieved correct plugin
        testObserver.assertSubscribed();
        testObserver.assertValue(dummyCachedFile);
        testObserver.dispose();
    }

    @Test
    public void AuroraInternalServiceCommunicator_shouldRemoveFileOnRequest() {
        // Subscribe to RemoveFromCacheResponse
        Observable<RemoveFromCacheResponse> observable = mBus.register(RemoveFromCacheResponse.class);

        // Create test observer for response
        TestObserver<Boolean> testObserver = new TestObserver<>();

        // Subscribe to observable
        observable.map(RemoveFromCacheResponse::isSuccess).subscribe(testObserver);

        // Create request file and post on bus
        RemoveFromCacheRequest request = new RemoveFromCacheRequest(fileRef, pluginName);
        mBus.post(request);

        // Check if cache removed the file correctly
        testObserver.assertSubscribed();
        testObserver.assertValue(true);
        testObserver.dispose();
    }

    @Test
    public void AuroraInternalServiceCommunicator_shouldRemoveAllFilesFromPluginOnRequest() {
        // Subscribe to RemoveFromCacheResponse
        Observable<RemoveFromCacheResponse> observable = mBus.register(RemoveFromCacheResponse.class);

        // Create test observer for response
        TestObserver<Boolean> testObserver = new TestObserver<>();

        // Subscribe to observable
        observable.map(RemoveFromCacheResponse::isSuccess).subscribe(testObserver);

        // Create request file and post on bus
        RemoveFromCacheRequest request = new RemoveFromCacheRequest(pluginName);
        mBus.post(request);

        // Check if cache removed the file correctly
        testObserver.assertSubscribed();
        testObserver.assertValue(true);
        testObserver.dispose();
    }

    @Test
    public void AuroraInternalServiceCommunicator_shouldClearCacheOnRequest() {
        // Subscribe to RemoveFromCacheResponse
        Observable<RemoveFromCacheResponse> observable = mBus.register(RemoveFromCacheResponse.class);

        // Create test observer for response
        TestObserver<Boolean> testObserver = new TestObserver<>();

        // Subscribe to observable
        observable.map(RemoveFromCacheResponse::isSuccess).subscribe(testObserver);

        // Create request file and post on bus
        RemoveFromCacheRequest request = new RemoveFromCacheRequest();
        mBus.post(request);

        // Check if cache removed the file correctly
        testObserver.assertSubscribed();
        testObserver.assertValue(true);
        testObserver.dispose();
    }

    /**
     * Dummy class with stub implementations for the cache
     */
    private static class DummyInternalCache extends InternalCache {
        /**
         * Creates an instance of the internal cache
         *
         * @param applicationContext the android application context
         */
        public DummyInternalCache(Context applicationContext) {
            super(applicationContext);
        }

        @Override
        public boolean cacheFile(String fileRef, String pluginObject, String uniquePluginName) {
            // Just return true
            return true;
        }

        @Override
        public CachedFileInfo checkCacheForProcessedFile(@NonNull String fileRef, @NonNull String uniquePluginName) {
            return dummyCachedFileInfo;
        }

        @Override
        public List<CachedFileInfo> getFullCache() {
            return dummyList;
        }

        @Override
        public List<CachedFileInfo> getFullCache(int amount) {
            return getFullCache();
        }

        @Override
        public CachedProcessedFile retrieveFile(String fileRef, String uniquePluginName) {
            return dummyCachedFile;
        }

        @Override
        public boolean removeFile(String fileRef, String uniquePluginName) {
            return true;
        }

        @Override
        public boolean removeFilesByPlugin(String uniquePluginName) {
            return true;
        }

        @Override
        public boolean clear() {
            return true;
        }
    }

    /**
     * Dummy implementation for testing purposes
     */
    private class DummyPluginObject extends PluginObject {
        public DummyPluginObject() {
            super("dummyfilename", "dummyplugin");
        }
    }
}
