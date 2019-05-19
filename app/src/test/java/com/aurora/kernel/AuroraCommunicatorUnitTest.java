package com.aurora.kernel;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;

import androidx.annotation.NonNull;

import com.aurora.auroralib.ExtractedText;
import com.aurora.internalservice.internalcache.CachedFileInfo;
import com.aurora.internalservice.internalcache.CachedProcessedFile;
import com.aurora.kernel.event.InternalProcessorRequest;
import com.aurora.kernel.event.InternalProcessorResponse;
import com.aurora.kernel.event.OpenCachedFileWithPluginRequest;
import com.aurora.kernel.event.OpenFileWithPluginRequest;
import com.aurora.kernel.event.QueryCacheResponse;
import com.aurora.kernel.event.RetrieveFileFromCacheRequest;
import com.aurora.kernel.event.RetrieveFileFromCacheResponse;
import com.aurora.plugin.Plugin;
import com.aurora.util.MockContext;

import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.TestObserver;
import io.reactivex.schedulers.Schedulers;


public class AuroraCommunicatorUnitTest {

    private static Bus sBus;
    private static AuroraCommunicator sAuroraCommunicator;

    private static final String UNIQUE_PLUGIN_NAME_DUMMY = "com.aurora.dummyplugin";
    private static final Plugin DUMMY_PLUGIN = new Plugin(UNIQUE_PLUGIN_NAME_DUMMY, "DummyPlugin", null, "Dummy plugin",
            1, "v0.1");

    @BeforeClass
    public static void initialize() {
        sBus = new Bus(Schedulers.trampoline());

        sAuroraCommunicator = new AuroraCommunicator(sBus, new MockContext());

    }

    @Test
    public void AuroraCommunicator_openFileWithPlugin_shouldSendProcessRequest() {
        // Subscribe to request
        Observable<InternalProcessorRequest> requestObservable = sBus.register(InternalProcessorRequest.class);

        // Create test observer
        TestObserver<String> fileRefObserver = new TestObserver<>();

        // Subscribe to observable
        requestObservable.map(InternalProcessorRequest::getFileRef).subscribe(fileRefObserver);

        // Call method under test
        String fileUri = "dummyUri";
        String fileRef = "Dummy/file/ref";
        String fileType = "txt";
        InputStream file = new DummyInputStream();
        sAuroraCommunicator.openFileWithPlugin(fileUri, fileRef, fileType, file, DUMMY_PLUGIN);

        // Assert that arguments passed are as expected
        fileRefObserver.assertSubscribed();
        fileRefObserver.assertValue(fileRef);
        fileRefObserver.dispose();
    }


    @Test
    public void AuroraCommunicator_openFileWithPlugin_shouldSendOpenFileWithPluginRequestAfterExtractingText() {

        // Create observable of internal processor request
        Observable<InternalProcessorRequest> internalProcessorRequestObservable = sBus.register(InternalProcessorRequest.class);

        // Subscribe to observable to send response event
        ExtractedText dummyExtractedText = new ExtractedText("Bla", "Bla", Arrays.asList("Dummy", "Paragraph"));
        Disposable internalProccessorRequestDisposable =
                internalProcessorRequestObservable.subscribe(internalProcessorRequest ->
                        sBus.post(new InternalProcessorResponse(dummyExtractedText)));

        // Create observable of open file with plugin request
        Observable<OpenFileWithPluginRequest> openFileWithPluginRequestObservable =
                sBus.register(OpenFileWithPluginRequest.class);

        // Create test observer
        TestObserver<ExtractedText> extractedTextObserver = new TestObserver<>();

        // Subscribe to observable
        openFileWithPluginRequestObservable
                .map(OpenFileWithPluginRequest::getExtractedText)
                .subscribe(extractedTextObserver);


        // Call the method under test
        String fileUri = "dummyUri";
        String dummyFileRef = "dummy/path/to/file";
        String fileType = "docx";
        InputStream file = new DummyInputStream();
        String pluginName = DUMMY_PLUGIN.getUniqueName();
        sAuroraCommunicator.openFileWithPlugin(fileUri, dummyFileRef, fileType, file, DUMMY_PLUGIN);

        // Assure that the correct values are contained in request event
        extractedTextObserver.assertSubscribed();
        extractedTextObserver.assertValue(dummyExtractedText);
        extractedTextObserver.dispose();

        internalProccessorRequestDisposable.dispose();
    }


    @Test
    public void AuroraCommunicator_openFileWithCache_shouldSendOpenFileWithCacheRequestAfterRetrievingFileFromCache() {
        // Create dummy arguments
        String fileRef = "dummyfileref";
        String fileType = "docx";
        String uniquePluginName = "com.aurora.dummyplugin";
        String jsonRepresentation = "{\"text\": \"Hello there\"}";

        // Create observable for QueryCacheRequests
        Observable<RetrieveFileFromCacheRequest> retrieveFileFromCacheRequestObservable =
                sBus.register(RetrieveFileFromCacheRequest.class);

        // Create fake CachedProcessedFile
        CachedProcessedFile cachedFile = new CachedProcessedFile(jsonRepresentation, fileRef, uniquePluginName);

        // Subscribe to observable to send response event
        Disposable disposable = retrieveFileFromCacheRequestObservable
                .subscribe(queryCacheRequest -> sBus.post(new RetrieveFileFromCacheResponse(cachedFile)));

        // Create observable of OpenCachedFileWithPluginRequests
        Observable<OpenCachedFileWithPluginRequest> openCachedFileWithPluginRequestObservable =
                sBus.register(OpenCachedFileWithPluginRequest.class);

        // Create test observers and subscribe to this observable
        TestObserver<String> jsonTestObserver = new TestObserver<>();
        TestObserver<String> pluginNameTestObserver = new TestObserver<>();

        openCachedFileWithPluginRequestObservable
                .map(OpenCachedFileWithPluginRequest::getJsonRepresentation)
                .subscribe(jsonTestObserver);

        openCachedFileWithPluginRequestObservable
                .map(OpenCachedFileWithPluginRequest::getUniquePluginName)
                .subscribe(pluginNameTestObserver);

        // Call method under test
        sAuroraCommunicator.openFileWithCache(fileRef, uniquePluginName);

        // Assert values
        jsonTestObserver.assertSubscribed();
        pluginNameTestObserver.assertSubscribed();
        jsonTestObserver.assertValue(jsonRepresentation);
        pluginNameTestObserver.assertValue(uniquePluginName);

        jsonTestObserver.dispose();
        pluginNameTestObserver.dispose();
        disposable.dispose();
    }

    @Test
    public void AuroraCommunicator_getListOfCachedFiles_shouldReturnListOfCachedFiles() {
        // Create test observer
        TestObserver<List<CachedFileInfo>> testObserver = new TestObserver<>();

        // Call method under test
        sAuroraCommunicator.getListOfCachedFiles(0, testObserver);


        // Make dummy list
        List<CachedFileInfo> cachedFilesList = new ArrayList<>();

        // Add fake cached file
        cachedFilesList.add(new CachedFileInfo("dummyfileref", "com.aurora.dummyplugin"));

        // Create response and post on bus
        QueryCacheResponse response = new QueryCacheResponse(cachedFilesList);
        sBus.post(response);

        // Assert values
        testObserver.assertSubscribed();
        testObserver.assertValue(cachedFilesList);
    }


    /**
     * Dummy stub class for testing purposes
     */
    private class DummyInputStream extends InputStream {

        @Override
        public int read() throws IOException {
            return 0;
        }
    }

    /**
     * Dummy stub class for testing purposes
     */
    private class DummyIntent extends Intent {
        @Override
        public ComponentName resolveActivity(@NonNull PackageManager pm) {
            return new ComponentName("com.aurora.dummyplugin", "MainActivity");
        }
    }
}
