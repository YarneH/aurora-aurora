package com.aurora.kernel;

import com.aurora.kernel.event.InternalProcessorResponse;
import com.aurora.plugin.ProcessedText;

import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import io.reactivex.Observable;
import io.reactivex.observers.TestObserver;

public class ProcessingCommunicatorTest {

    private static Bus mBus;
    private static ProcessingCommunicator mProcessingCommunicator;

    @BeforeClass
    public static void initialize() {
        mBus = new Bus();
        mProcessingCommunicator = new ProcessingCommunicator(mBus);
    }

    @Test
    public void ProcessingCommunicator_processFileWithAuroraProcessor_shouldReturnProcessedText() {
        // Create dummy parameters
        String fileRef = "/path/to/dummy/file";
        String title = "Dummy Title";

        // Convert array to list
        List<String> paragraphs =
                Arrays.stream(new String[]{"Paragraph 1", "Paragraph 2", "Paragraph 3"})
                        .collect(Collectors.toList());

        // Create test observer to subscribe to observable
        TestObserver<ProcessedText> observer = new TestObserver<>();

        // Call method under test
        Observable<ProcessedText>
                processedTextObservable = mProcessingCommunicator.processFileWithAuroraProcessor(fileRef);

        // Make dummy processed text
        ProcessedText text = new ProcessedText(title, paragraphs);

        // Subscribe to observable
        processedTextObservable.subscribe(observer);

        // Make response event
        InternalProcessorResponse response = new InternalProcessorResponse(text);

        // Post response event
        mBus.post(response);

        // Assert values
        observer.assertSubscribed();
        observer.assertValue(text);
    }
}
