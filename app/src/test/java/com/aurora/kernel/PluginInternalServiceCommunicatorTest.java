package com.aurora.kernel;

import com.aurora.internalservice.internalprocessor.ExtractedText;
import com.aurora.internalservice.internalprocessor.FileTypeNotSupportedException;
import com.aurora.internalservice.internalprocessor.InternalTextProcessing;
import com.aurora.kernel.event.InternalProcessorRequest;
import com.aurora.kernel.event.InternalProcessorResponse;

import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.observers.TestObserver;

public class PluginInternalServiceCommunicatorTest {

    private static Bus mBus;
    private static InternalTextProcessing mProcessing;
    private static PluginInternalServiceCommunicator mCommunicator;

    private static String mTitle = "Dummy Title";
    private static List<String> mParagraphs = Arrays.asList("Paragraph1", "Paragraph2");
    private static ExtractedText mExtractedText;

    @BeforeClass
    public static void initialize() {
        // Initialize bus
        mBus = new Bus();

        // Initialize processor
        mProcessing = new DummyInternalTextProcessing();

        // Initialize communicator
        mCommunicator = new PluginInternalServiceCommunicator(mBus, mProcessing);

        // Initialize extracted text with dummy contents
        mExtractedText = new ExtractedText(mTitle, mParagraphs);
    }

    @Test
    public void PluginInternalServiceCommunicator_processFileWithInternalProcessor_shouldPostExtractedTextResponse() {
        // Fake string ref
        String ref = "Fake/path/to/file.pdf";

        // Listen for internal processor response
        Observable<InternalProcessorResponse> observable = mBus.register(InternalProcessorResponse.class);

        // Create test observer
        TestObserver<ExtractedText> testObserver = new TestObserver<>();

        // Subscribe to observable
        observable.map(InternalProcessorResponse::getExtractedText).subscribe(testObserver);

        // Create request to process file and put on bus
        InternalProcessorRequest request = new InternalProcessorRequest(ref);
        mBus.post(request);

        // Assert that dummy extracted text was received
        testObserver.assertSubscribed();
        testObserver.assertValue(mExtractedText);
    }

    private static class DummyInternalTextProcessing extends InternalTextProcessing {

        /**
         * Dummy method that will just return a fake extracted text
         *
         * @param fileRef a reference to where the file can be found
         * @return dummy extracted text
         * @throws FileTypeNotSupportedException
         */
        @Override
        public ExtractedText processFile(String fileRef) throws FileTypeNotSupportedException {
            // Just return the dummy extracted text
            return mExtractedText;
        }
    }
}
