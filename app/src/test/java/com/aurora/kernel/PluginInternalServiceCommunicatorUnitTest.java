package com.aurora.kernel;

import com.aurora.auroralib.ExtractedText;
import com.aurora.auroralib.Section;
import com.aurora.internalservice.internalprocessor.FileTypeNotSupportedException;
import com.aurora.internalservice.internalprocessor.InternalTextProcessor;
import com.aurora.kernel.event.InternalProcessorRequest;
import com.aurora.kernel.event.InternalProcessorResponse;
import com.aurora.plugin.InternalServices;
import com.aurora.plugin.Plugin;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.AnnotationPipeline;
import edu.stanford.nlp.pipeline.POSTaggerAnnotator;
import edu.stanford.nlp.pipeline.TokenizerAnnotator;
import edu.stanford.nlp.pipeline.WordsToSentencesAnnotator;
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
        mCommunicator = new PluginInternalServiceCommunicator(mBus, mProcessor);

        // Initialize extracted text with dummy contents
        mExtractedText = new ExtractedText(mTitle, null, mParagraphs);
    }

    @Test
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

    @Test
    public void PluginInternalServiceCommunicator_processFileWithInternalProcessor_shouldDoNLPWhenAsked(){
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
        List<InternalServices> internalServices =
                new ArrayList<>(Arrays.asList(
                        InternalServices.TEXT_EXTRACTION,
                        InternalServices.IMAGE_EXTRACTION,
                        InternalServices.NLP_TOKENIZE,
                        InternalServices.NLP_SSPLIT,
                        InternalServices.NLP_POS
                ));

        InternalProcessorRequest request = new InternalProcessorRequest(ref, type, null,
                internalServices);
        mBus.post(request);

        ExtractedText extractedText = testObserver.values().get(0);

        AnnotationPipeline annotationPipeline = new AnnotationPipeline();
        annotationPipeline.addAnnotator(new TokenizerAnnotator(false, "en"));
        annotationPipeline.addAnnotator(new WordsToSentencesAnnotator(false));
        annotationPipeline.addAnnotator(new POSTaggerAnnotator(false));



        if(extractedText.getTitle() != null) {
            Assert.assertNotNull(extractedText.getTitleAnnotation());

            Annotation annotation = new Annotation(extractedText.getTitle());
            annotationPipeline.annotate(annotation);

            assert (extractedText.getTitleAnnotation().equals(annotation));
        }

        for (Section section: extractedText.getSections()) {
            if(section.getBody() != null) {
                Assert.assertNotNull(section.getBodyAnnotation());

                Annotation annotation = new Annotation(section.getBody());
                annotationPipeline.annotate(annotation);

                assert (section.getBodyAnnotation().equals(annotation));
            }
            if(section.getTitle() != null) {
                Assert.assertNotNull(section.getTitleAnnotation());

                Annotation annotation = new Annotation(section.getTitle());
                annotationPipeline.annotate(annotation);

                assert (section.getTitleAnnotation().equals(annotation));
            }
        }

        testObserver.dispose();
    }

    @Test
    public void PluginInternalServiceCommunicator_processFileWithInternalProcessor_shouldNotDoNLPWhenNotAsked(){
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
        List<InternalServices> internalServices =
                new ArrayList<>(Arrays.asList(
                        InternalServices.TEXT_EXTRACTION,
                        InternalServices.IMAGE_EXTRACTION
                ));

        InternalProcessorRequest request = new InternalProcessorRequest(ref, type, null,
                internalServices);
        mBus.post(request);

        ExtractedText extractedText = testObserver.values().get(0);

        Assert.assertNull(extractedText.getTitleAnnotation());
        for (Section section: extractedText.getSections()) {
            Assert.assertNull(section.getTitleAnnotation());
            Assert.assertNull(section.getBodyAnnotation());
        }

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
         * @param extractImages
         * @return dummy extracted text
         * @throws FileTypeNotSupportedException thrown when a file with an unsupported extension is opened
         */
        @Override
        public ExtractedText processFile(InputStream file, String fileRef, String type,
                                         boolean extractImages) throws FileTypeNotSupportedException {
            // Just return the dummy extracted text
            return mExtractedText;
        }
    }
}
