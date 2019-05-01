package com.aurora.kernel;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

import androidx.annotation.NonNull;

import com.aurora.auroralib.ExtractedText;
import com.aurora.kernel.event.InternalProcessorRequest;
import com.aurora.kernel.event.InternalProcessorResponse;
import com.aurora.kernel.event.ListPluginsResponse;
import com.aurora.kernel.event.OpenFileWithPluginChooserRequest;
import com.aurora.kernel.event.OpenFileWithPluginRequest;
import com.aurora.plugin.Plugin;
import com.aurora.util.MockContext;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.observers.TestObserver;
import io.reactivex.schedulers.Schedulers;


public class AuroraCommunicatorUnitTest {

    private static Bus sBus;
    private static DummyPluginRegistry sPluginRegistry;
    private static AuroraCommunicator sAuroraCommunicator;

    private static final String UNIQUE_PLUGIN_NAME_DUMMY = "com.aurora.dummyplugin";
    private static final Plugin DUMMY_PLUGIN = new Plugin(UNIQUE_PLUGIN_NAME_DUMMY, "DummyPlugin", null, "Dummy plugin",
            1 ,"v0.1");

    @BeforeClass
    public static void initialize() {
        sBus = new Bus(Schedulers.trampoline());

        ProcessingCommunicator processingCommunicator = new ProcessingCommunicator(sBus);
        final String pluginsCfg = "plugin-config.json";
        sPluginRegistry = new DummyPluginRegistry(processingCommunicator, pluginsCfg, new MockContext());

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
        String pluginName = DUMMY_PLUGIN.getUniqueName();
        sAuroraCommunicator.openFileWithPlugin(fileRef, fileType, file, pluginName, new MockContext());

        // Assert that arguments passed are as expected
        fileRefObserver.assertSubscribed();
        fileRefObserver.assertValue(fileRef);
        fileRefObserver.dispose();
    }

    //TODO delete when custom picker is finished
    @Test
    public void AuroraCommunicator_openFileWithPluginChooser_shouldSendProcessRequest() {
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
        sAuroraCommunicator.openFileWithPluginChooser(fileRef, fileType, file, new DummyIntent(),
                new DummyIntent(), new MockContext());

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
        String pluginName = DUMMY_PLUGIN.getUniqueName();
        sAuroraCommunicator.openFileWithPlugin(dummyFileRef, fileType, file, pluginName, new MockContext());

        // Assure that the correct values are contained in request event
        extractedTextObserver.assertSubscribed();
        extractedTextObserver.assertValue(dummyExtractedText);
        extractedTextObserver.dispose();
    }

    //TODO: delete when custom picker is finished
    // This test works by itself but not if the test before this one is executed
    /*
    @Test
    public void AuroraCommunicator_openFileWithPluginChooser_shouldSendOpenFileWithPluginChooserRequestAfterExtractingText() {
        // Create observable of internal processor request
        Observable<InternalProcessorRequest> internalProcessorRequestObservable = sBus.register(InternalProcessorRequest.class);

        // Subscribe to observable to send response event
        ExtractedText dummyExtractedText = new ExtractedText("Bla", null, Arrays.asList("Dummy", "Paragraph"));
        internalProcessorRequestObservable.subscribe(internalProcessorRequest ->
                sBus.post(new InternalProcessorResponse(dummyExtractedText)));

        // Create observable of open file with plugin request
        Observable<OpenFileWithPluginChooserRequest> openFileWithPluginChooserRequestObservable =
                sBus.register(OpenFileWithPluginChooserRequest.class);

        // Create test observer
        TestObserver<ExtractedText> extractedTextObserver = new TestObserver<>();

        // Subscribe to observable
        openFileWithPluginChooserRequestObservable
                .map(OpenFileWithPluginChooserRequest::getExtractedText)
                .subscribe(extractedTextObserver);


        // Call the method under test
        String dummyFileRef = "dummy/path/to/file";
        String fileType = "docx";
        InputStream file = new DummyInputStream();
        String pluginName = DUMMY_PLUGIN.getUniqueName();
        sAuroraCommunicator.openFileWithPluginChooser(dummyFileRef, fileType, file,
                new DummyIntent(), new DummyIntent(), new MockContext());

        // Assure that the correct values are contained in request event
        extractedTextObserver.assertSubscribed();
        extractedTextObserver.assertValue(dummyExtractedText);
        extractedTextObserver.dispose();
    }
    */


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

    @Test
    public void AuroraCommunicator_registerPlugin_shouldRegisterPLuginInPluginRegistry() {
        // Create dummy plugin
        String uniquePluginName = "com.aurora.dummyplugin2";
        String pluginName = "Dummy Plugin";
        String description = "Dummy plugin description";
        int versionNumber = 1;
        String versionCode = "v0.1";

        Plugin dummyPlugin = new Plugin(uniquePluginName, pluginName, null,
                description, versionNumber, versionCode);

        // Call register plugin method
        boolean registerResult = sAuroraCommunicator.registerPlugin(dummyPlugin);

        // Assert that the returned result is true
        Assert.assertTrue(registerResult);

        // Assert that the plugins map actually contains the plugin
        Assert.assertTrue(sPluginRegistry.mPluginMap.containsKey(uniquePluginName));

        // Assert that the plugin info is equal to the one that was entered
        Assert.assertEquals(dummyPlugin, sPluginRegistry.mPluginMap.get(uniquePluginName));
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

    private static class DummyPluginRegistry extends PluginRegistry {

        Map<String, Plugin> mPluginMap = new HashMap<>();

        DummyPluginRegistry(ProcessingCommunicator processingCommunicator, String configFileRef, Context context) {
            super(processingCommunicator, configFileRef, context);
        }

        @Override
        boolean registerPlugin(String pluginName, Plugin plugin) {
            if (!mPluginMap.containsKey(pluginName)) {
                mPluginMap.put(pluginName, plugin);
                return true;
            }

            return false;
        }

        @Override
        public Plugin getPlugin(String pluginName) {
            return mPluginMap.get(pluginName);
        }
    }
}
