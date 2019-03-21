package com.aurora.kernel;

import android.app.Activity;
import android.support.v4.app.Fragment;

import com.aurora.internalservice.internalprocessor.ExtractedText;
import com.aurora.kernel.event.InternalProcessorRequest;
import com.aurora.kernel.event.InternalProcessorResponse;
import com.aurora.kernel.event.ListPluginsResponse;
import com.aurora.kernel.event.OpenFileWithPluginRequest;
import com.aurora.plugin.BasicPlugin;

import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.observers.TestObserver;


public class AuroraCommunicatorTest {

    private static Bus mBus;
    private static AuroraCommunicator mAuroraCommunicator;

    @BeforeClass
    public static void initialize() {
        mBus = new Bus();
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
        String pluginName = "DummyPlugin";
        mAuroraCommunicator.openFileWithPlugin(fileRef, pluginName);

        // Assert that arguments passed are as expected
        fileRefObserver.assertSubscribed();
        fileRefObserver.assertValue(fileRef);
    }

    @Test
    public void AuroraCommunicator_openFileWithPlugin_shouldSendOpenFileWithPluginRequestAfterExtractingText() {
        // Create observable of internal processor request
        Observable<InternalProcessorRequest> internalProcessorRequestObservable = mBus.register(InternalProcessorRequest.class);

        // Subscribe to observable to send response event
        ExtractedText dummyExtractedText = new ExtractedText("Bla", Arrays.asList("Dummy", "Paragraph"));
        internalProcessorRequestObservable.subscribe(internalProcessorRequest ->
                mBus.post(new InternalProcessorResponse(dummyExtractedText)));

        // Create observable of open file with plugin request
        Observable<OpenFileWithPluginRequest> openFileWithPluginRequestObservable =
                mBus.register(OpenFileWithPluginRequest.class);

        // Create test observers
        TestObserver<ExtractedText> extractedTextObserver = new TestObserver<>();
        TestObserver<String> pluginNameObserver = new TestObserver<>();

        // Subscribe to observable
        openFileWithPluginRequestObservable
                .map(OpenFileWithPluginRequest::getExtractedText)
                .subscribe(extractedTextObserver);

        openFileWithPluginRequestObservable
                .map(OpenFileWithPluginRequest::getPluginName)
                .subscribe(pluginNameObserver);

        // Call the method under test
        String dummyFileRef = "dummy/path/to/file";
        String dummyPluginName = "DummyPlugin";
        mAuroraCommunicator.openFileWithPlugin(dummyFileRef, dummyPluginName);

        // Assure that the correct values are contained in request event
        extractedTextObserver.assertSubscribed();
        extractedTextObserver.assertValue(dummyExtractedText);

        pluginNameObserver.assertSubscribed();
        pluginNameObserver.assertValue(dummyPluginName);
    }

    @Test
    public void AuroraCommunicator_getListOfPlugins_shouldReturnListOfPlugins() {
        // Create dummy arguments
        String pluginName = "DummyPlugin";
        String pluginDescription = "this is a dummy description.";
        String pluginVersion = "0.1";

        // Create observer to subscribe to observable
        TestObserver<List<BasicPlugin>> observer = new TestObserver<>();

        // Call the method under test
        Observable<List<BasicPlugin>> listObservable = mAuroraCommunicator.getListOfPlugins();

        // Make dummy list
        List<BasicPlugin> basicPluginList = new ArrayList<>();

        // Add fake basic plugin
        basicPluginList.add(new BasicPlugin(pluginName, null, pluginDescription, pluginVersion));

        // Make response containing the list
        ListPluginsResponse response = new ListPluginsResponse(basicPluginList);

        // Subscribe to observable and assert that list is what expected
        listObservable.subscribe(observer);

        // Post response
        mBus.post(response);

        // Assert values
        observer.assertSubscribed();
        observer.assertValue(basicPluginList);
    }

    /**
     * Private dummy activity class for testing purposes
     */
    private class DummyActivity extends Activity {
    }

    /**
     * Private dummy fragment class for testing purposes
     */
    private class DummyFragment extends Fragment {
    }
}
