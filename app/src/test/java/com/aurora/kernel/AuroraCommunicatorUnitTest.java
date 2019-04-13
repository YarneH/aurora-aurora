package com.aurora.kernel;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;

import com.aurora.auroralib.ExtractedText;
import com.aurora.kernel.event.InternalProcessorRequest;
import com.aurora.kernel.event.InternalProcessorResponse;
import com.aurora.kernel.event.ListPluginsResponse;
import com.aurora.kernel.event.OpenFileWithPluginRequest;
import com.aurora.plugin.Plugin;
import com.aurora.util.MockContext;

import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import androidx.annotation.NonNull;
import io.reactivex.Observable;
import io.reactivex.observers.TestObserver;
import io.reactivex.schedulers.Schedulers;


public class AuroraCommunicatorUnitTest {

    private static Bus sBus;
    private static PluginRegistry sPluginRegistry;
    private static AuroraCommunicator sAuroraCommunicator;

    private static final String UNIQUE_PLUGIN_NAME_DUMMY = "com.aurora.dummyplugin";
    private static final Plugin DUMMY_PLUGIN = new Plugin(UNIQUE_PLUGIN_NAME_DUMMY, "DummyPlugin", null, "Dummy plugin",
            1 ,"v0.1");

    @BeforeClass
    public static void initialize() {
        sBus = new Bus(Schedulers.trampoline());

        ProcessingCommunicator processingCommunicator = new ProcessingCommunicator(sBus);
        final String pluginsCfg = "plugin-config.json";
        sPluginRegistry = new PluginRegistry(processingCommunicator, pluginsCfg, new MockContext());

        sAuroraCommunicator = new AuroraCommunicator(sBus, sPluginRegistry);

        // Register dummy plugin in registry
        sAuroraCommunicator.registerPlugin(DUMMY_PLUGIN);
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
        String fileRef = "Dummy/file/ref";
        String fileType = "txt";
        InputStream file = new DummyInputStream();
        sAuroraCommunicator.openFileWithPlugin(fileRef, fileType, file,
                new DummyIntent(), new DummyIntent(), new MockContext());

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
        ExtractedText dummyExtractedText = new ExtractedText("Bla", null, Arrays.asList("Dummy", "Paragraph"));
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
        String dummyFileRef = "dummy/path/to/file";
        String fileType = "docx";
        InputStream file = new DummyInputStream();
        sAuroraCommunicator.openFileWithPlugin(dummyFileRef, fileType, file,
                new DummyIntent(), new DummyIntent(), new MockContext());

        // Assure that the correct values are contained in request event
        extractedTextObserver.assertSubscribed();
        extractedTextObserver.assertValue(dummyExtractedText);
        extractedTextObserver.dispose();
    }


    @Test
    public void AuroraCommunicator_getListOfPlugins_shouldReturnListOfPlugins() {
        // Create dummy arguments
        String uniquePluginName = "com.aurora.dummyplugin";
        String pluginName = "DummyPlugin";
        String pluginDescription = "this is a dummy description.";
        int pluginVersionNumber = 1;
        String pluginVersionCode = "v0.1";

        // Create observer to subscribe to observable
        TestObserver<List<Plugin>> observer = new TestObserver<>();

        // Call the method under test
        Observable<List<Plugin>> listObservable = sAuroraCommunicator.getListOfPlugins();

        // Make dummy list
        List<Plugin> pluginList = new ArrayList<>();

        // Add fake basic plugin
        pluginList.add(new Plugin(uniquePluginName, pluginName, null,
                pluginDescription, pluginVersionNumber, pluginVersionCode));

        // Make response containing the list
        ListPluginsResponse response = new ListPluginsResponse(pluginList);

        // Subscribe to observable and assert that list is what expected
        listObservable.subscribe(observer);

        // Post response
        sBus.post(response);

        // Assert values
        observer.assertSubscribed();
        observer.assertValue(pluginList);
        observer.dispose();
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
