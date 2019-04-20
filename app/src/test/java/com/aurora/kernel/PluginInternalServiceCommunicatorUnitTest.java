package com.aurora.kernel;

import com.aurora.auroralib.ExtractedText;
import com.aurora.internalservice.internalprocessor.FileTypeNotSupportedException;
import com.aurora.internalservice.internalprocessor.InternalTextProcessor;
import com.aurora.internalservice.internaltranslation.Translator;
import com.aurora.kernel.event.InternalProcessorRequest;
import com.aurora.kernel.event.InternalProcessorResponse;
import com.aurora.plugin.Plugin;
import com.aurora.util.MockContext;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.observers.TestObserver;
import io.reactivex.schedulers.Schedulers;

public class PluginInternalServiceCommunicatorUnitTest {

    private static Bus mBus;
    private static InternalTextProcessor mProcessor;
    private static PluginInternalServiceCommunicator mCommunicator;

    private static String mTitle = "Dummy Title";
    private static List<String> mParagraphs = Arrays.asList("Paragraph1", "Paragraph2");
    private static ExtractedText mExtractedText;

    @BeforeClass
    public static void initialize() {
        // Initialize bus
        mBus = new Bus(Schedulers.trampoline());

        // Initialize processor
        mProcessor = new DummyInternalTextProcessing();

        // Initialize communicator
        // add dummy translator
        mCommunicator = new PluginInternalServiceCommunicator(mBus, mProcessor, new Translator(), new MockContext());

        // Initialize extracted text with dummy contents
        mExtractedText = new ExtractedText(mTitle, null, mParagraphs);
    }

    @Test
    @Ignore("Volley objects in PluginInternalServiceCommunicator not mocked yet")
    public void PluginInternalServiceCommunicator_processFileWithInternalProcessor_shouldPostExtractedTextResponse() {
        // Fake string ref
        String ref = "Fake/path/to/file";
        String type = "docx";

        // Listen for internal processor response
        Observable<InternalProcessorResponse> observable = mBus.register(InternalProcessorResponse.class);

        // Create test observer
        TestObserver<ExtractedText> testObserver = new TestObserver<>();

        // Subscribe to observable
        observable.map(InternalProcessorResponse::getExtractedText).subscribe(testObserver);

        // Create request to process file and put on bus
        InternalProcessorRequest request = new InternalProcessorRequest(ref, type, null,
                Plugin.getDefaultInternalServices());
        mBus.post(request);

        // Assert that dummy extracted text was received
        testObserver.assertSubscribed();
        testObserver.assertValue(mExtractedText);
        testObserver.dispose();
    }

    /**
     * Private dummy processing class for testing purposes.
     * Acts as a stub for a real internal text processor.
     */
    private static class DummyInternalTextProcessing extends InternalTextProcessor {

        /**
         * Dummy method that will just return a fake extracted text
         *
         * @param fileRef a reference to where the file can be found
         * @return dummy extracted text
         * @throws FileTypeNotSupportedException thrown when a file with an unsupported extension is opened
         */
        @Override
        public ExtractedText processFile(InputStream file, String fileRef, String type) throws FileTypeNotSupportedException {
            // Just return the dummy extracted text
            return mExtractedText;
        }
    }
}
