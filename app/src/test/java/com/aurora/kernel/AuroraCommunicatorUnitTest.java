package com.aurora.kernel;

import android.content.Context;
import android.content.Intent;

import com.aurora.auroralib.Constants;
import com.aurora.auroralib.ExtractedText;
import com.aurora.kernel.event.InternalProcessorRequest;
import com.aurora.kernel.event.InternalProcessorResponse;
import com.aurora.kernel.event.ListPluginsResponse;
import com.aurora.kernel.event.OpenFileWithPluginRequest;
import com.aurora.plugin.Plugin;

import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.observers.TestObserver;
import io.reactivex.schedulers.Schedulers;


public class AuroraCommunicatorUnitTest {

    private static Bus mBus;
    private static AuroraCommunicator mAuroraCommunicator;

    @BeforeClass
    public static void initialize() {
        mBus = new Bus(Schedulers.trampoline());
        mAuroraCommunicator = new AuroraCommunicator(mBus);
    }

    @Test
    public void AuroraCommunicator_openFileWithPlugin_shouldSendProcessRequest() {
        // Subscribe to request
        Observable<InternalProcessorRequest> requestObservable = mBus.register(InternalProcessorRequest.class);

        // Create test observer
        TestObserver<String> fileRefObserver = new TestObserver<>();

        // Subscribe to observable
        requestObservable.map(InternalProcessorRequest::getFileRef).subscribe(fileRefObserver);

        // Call method under test
        String fileRef = "Dummy/file/ref";
        InputStream file = new DummyInputStream();
        Intent targetPlugin = new Intent(Constants.PLUGIN_ACTION);
        mAuroraCommunicator.openFileWithPlugin(fileRef, "docx" ,file, targetPlugin, new Context());

        // Assert that arguments passed are as expected
        fileRefObserver.assertSubscribed();
        fileRefObserver.assertValue(fileRef);
        fileRefObserver.dispose();
    }

    @Test
    public void AuroraCommunicator_openFileWithPlugin_shouldSendOpenFileWithPluginRequestAfterExtractingText() {
        // Create observable of internal processor request
        Observable<InternalProcessorRequest> internalProcessorRequestObservable = mBus.register(InternalProcessorRequest.class);

        // Subscribe to observable to send response event
        ExtractedText dummyExtractedText = new ExtractedText("Bla", null, Arrays.asList("Dummy", "Paragraph"));
        internalProcessorRequestObservable.subscribe(internalProcessorRequest ->
                mBus.post(new InternalProcessorResponse(dummyExtractedText)));

        // Create observable of open file with plugin request
        Observable<OpenFileWithPluginRequest> openFileWithPluginRequestObservable =
                mBus.register(OpenFileWithPluginRequest.class);

        // Create test observer
        TestObserver<ExtractedText> extractedTextObserver = new TestObserver<>();

        // Subscribe to observable
        openFileWithPluginRequestObservable
                .map(OpenFileWithPluginRequest::getExtractedText)
                .subscribe(extractedTextObserver);


        // Call the method under test
        String dummyFileRef = "dummy/path/to/file";
        InputStream file = new DummyInputStream();
        Intent dummyPlugin = new Intent(Constants.PLUGIN_ACTION);
        mAuroraCommunicator.openFileWithPlugin(dummyFileRef, "docx", file, dummyPlugin, new Context());

        // Assure that the correct values are contained in request event
        extractedTextObserver.assertSubscribed();
        extractedTextObserver.assertValue(dummyExtractedText);
        extractedTextObserver.dispose();
    }


    @Test
    public void AuroraCommunicator_getListOfPlugins_shouldReturnListOfPlugins() {
        // Create dummy arguments
        String pluginName = "DummyPlugin";
        String pluginDescription = "this is a dummy description.";
        String pluginVersion = "0.1";

        // Create observer to subscribe to observable
        TestObserver<List<Plugin>> observer = new TestObserver<>();

        // Call the method under test
        Observable<List<Plugin>> listObservable = mAuroraCommunicator.getListOfPlugins();

        // Make dummy list
        List<Plugin> pluginList = new ArrayList<>();

        // Add fake basic plugin
        pluginList.add(new Plugin(pluginName, pluginName, null, pluginDescription, pluginVersion));

        // Make response containing the list
        ListPluginsResponse response = new ListPluginsResponse(pluginList);

        // Subscribe to observable and assert that list is what expected
        listObservable.subscribe(observer);

        // Post response
        mBus.post(response);

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

}
