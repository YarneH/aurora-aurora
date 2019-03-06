package com.aurora.kernel;

import com.aurora.internalservice.internalprocessor.ExtractedText;
import com.aurora.kernel.event.PluginProcessorResponse;
import com.aurora.plugin.BasicProcessedText;
import com.aurora.processingservice.PluginProcessor;
import com.aurora.plugin.ProcessedText;

import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.observers.TestObserver;

public class PluginCommunicatorTest {

    private static Bus mBus;
    private static ProcessingCommunicator mProcessingCommunicator;
    private static PluginRegistry mPluginRegistry;
    private static PluginCommunicator mPluginCommunicator;
    private static final String mPluginConfigFileRef = "plugins.cfg";

    @BeforeClass
    public static void initialize() {
        mBus = new Bus();

        mProcessingCommunicator = new ProcessingCommunicator(mBus);
        mPluginRegistry = new PluginRegistry(mProcessingCommunicator, mPluginConfigFileRef);
        mPluginCommunicator = new PluginCommunicator(mBus, mPluginRegistry);
    }

    @Test
    public void PluginCommunicator_processFileWithPlugin_shouldReturnProcessedFile() {
        // Create dummy arguments
        PluginProcessor processor = new DummyPluginProcessor(mProcessingCommunicator);
        String fileRef = "/path/to/dummy/file";

        // Create test observer to subscribe to the observable
        TestObserver<ProcessedText> observer = new TestObserver<>();

        // Call method under test
        Observable<ProcessedText> processedTextObservable = mPluginCommunicator.processFileWithPluginProcessor(processor, fileRef);

        // Create dummy processed text
        String title = "Title";
        List<String> paragraphs = Arrays.asList("Paragraph 1", "Paragraph 2", "Paragraph 3");
        ProcessedText text = new BasicProcessedText(title, paragraphs);

        // Create response
        PluginProcessorResponse response = new PluginProcessorResponse(text);

        // Subscribe to observable
        processedTextObservable.subscribe(observer);

        // Post response
        mBus.post(response);

        // Assert values
        observer.assertSubscribed();
        observer.assertValue(text);
    }

    /**
     * Dummy plugin processor for testing purposes
     */
    private class DummyPluginProcessor extends PluginProcessor {

        public DummyPluginProcessor(ProcessingCommunicator processingCommunicator) {
            super(processingCommunicator);
        }

        @Override
        public ProcessedText processFileWithPluginProcessor(String fileRef) {
            return null;
            //TODO
        }

        @Override
        protected void resultProcessFileWithAuroraProcessor(ExtractedText extractedText){
            //TODO
        }
    }
}
