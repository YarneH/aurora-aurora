package com.aurora.kernel;

import android.os.Build;

import com.aurora.auroralib.ExtractedText;
import com.aurora.auroralib.Section;
import com.aurora.internalservice.internalprocessor.DocumentNotSupportedException;
import com.aurora.internalservice.internalprocessor.FileTypeNotSupportedException;
import com.aurora.internalservice.internalprocessor.InternalTextProcessor;
import com.aurora.internalservice.internaltranslation.Translator;
import com.aurora.kernel.event.InternalProcessorRequest;
import com.aurora.kernel.event.InternalProcessorResponse;
import com.aurora.plugin.InternalServices;
import com.aurora.plugin.Plugin;
import com.aurora.util.FakeRequestQueue;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
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
    private static InternalTextProcessor mInternalTextProcessor;
    private static PluginInternalServiceCommunicator mCommunicator;

    private static String mTitle = "Dummy Title";
    private static String mFileUri = "dummyFileUri";
    private static String mFileRef = "src/test/res/Pasta.txt";
    private static String mFileType = "txt";
    private static List<String> mParagraphs = Arrays.asList("Paragraph1", "Paragraph2");
    private static ExtractedText mExtractedText;
    private InputStream mInputStream = null;

    @BeforeClass
    public static void initialize() {
        // Initialize bus
        mBus = new Bus(Schedulers.trampoline());

        // Initialize processor
        mInternalTextProcessor = new DummyInternalTextProcessing();

        // Initialize communicator
        // add dummy translator

        mCommunicator = new PluginInternalServiceCommunicator(mBus, mInternalTextProcessor, new Translator(new FakeRequestQueue()));

        // Initialize extracted text with dummy contents
        mExtractedText = new ExtractedText(mFileUri, mTitle, mParagraphs);
    }

    @Before
    public void readFile() {
        File file = new File(mFileRef);
        try {
            mInputStream = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @After
    public void cleanUp() throws NoSuchFieldException, IllegalAccessException{
        try {
            mInputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // Set the SDK version back to 22
        setFinalStatic(Build.VERSION.class.getField("SDK_INT"), Build.VERSION_CODES.LOLLIPOP_MR1);

    }

    @Test
    public void PluginInternalServiceCommunicator_processFileWithInternalProcessor_shouldPostExtractedTextResponse() {
        // Listen for internal processor response
        Observable<InternalProcessorResponse> observable = mBus.register(InternalProcessorResponse.class);

        // Create test observer
        TestObserver<ExtractedText> testObserver = new TestObserver<>();

        // Subscribe to observable
        observable.map(InternalProcessorResponse::getExtractedText).subscribe(testObserver);

        // Create request to process file and put on bus
        InternalProcessorRequest request = new InternalProcessorRequest(mFileUri, mFileRef, mFileType, mInputStream,
                Plugin.getDefaultInternalServices());
        mBus.post(request);

        // Assert that dummy extracted text was received
        testObserver.assertSubscribed();
        testObserver.assertValue(mExtractedText);

        //testObserver.assertValue(mExtractedText);
        testObserver.dispose();
    }

    @Test
    public void PluginInternalServiceCommunicator_processFileWithInternalProcessor_shouldDoNLPWhenAsked()
    throws IllegalAccessException, NoSuchFieldException{
        // Listen for internal processor response
        Observable<InternalProcessorResponse> observable = mBus.register(InternalProcessorResponse.class);

        // Create test observer
        TestObserver<ExtractedText> testObserver = new TestObserver<>();

        // Subscribe to observable
        observable.map(InternalProcessorResponse::getExtractedText).subscribe(testObserver);

        // Set the SDK version to 26 (minimum for NLP)
        // Can throw the exceptions
        setFinalStatic(Build.VERSION.class.getField("SDK_INT"), Build.VERSION_CODES.O);

        // Create request to process file and put on bus
        List<InternalServices> internalServices =
                new ArrayList<>(Arrays.asList(
                        InternalServices.TEXT_EXTRACTION,
                        InternalServices.IMAGE_EXTRACTION,
                        InternalServices.NLP_TOKENIZE,
                        InternalServices.NLP_SSPLIT,
                        InternalServices.NLP_POS
                ));

        InternalProcessorRequest request = new InternalProcessorRequest(mFileUri, mFileRef, mFileType, mInputStream,
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
            if(!section.getTitle().isEmpty()) {
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

        InternalProcessorRequest request = new InternalProcessorRequest(mFileUri, mFileRef, mFileType, mInputStream,
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
         * @param fileUri the uri of the file
         * @param fileRef the name of the file
         * @param extractImages boolean indicating whether or not to extract images
         * @return dummy extracted text
         * @throws FileTypeNotSupportedException thrown when a file with an unsupported extension is opened
         */
        @Override
        public ExtractedText processFile(InputStream file, String fileUri, String fileRef, String type,
                                         boolean extractImages) throws FileTypeNotSupportedException, DocumentNotSupportedException {
            // Just return the dummy extracted text
            mExtractedText = super.processFile(file, fileUri, fileRef, type, extractImages);
            return mExtractedText;
        }
    }

    /**
     * Private method to set final static values during tests (e.g. Build Version)
     *
     * @param field                     Field to set
     * @param newValue                  Value to be set
     * @throws IllegalAccessException   thrown if the field cannot be accessed
     * @throws NoSuchFieldException     thrown if the field does not exist
     */
    private static void setFinalStatic(Field field, Object newValue)
            throws IllegalAccessException, NoSuchFieldException {
        field.setAccessible(true);

        Field modifiersField = Field.class.getDeclaredField("modifiers");
        modifiersField.setAccessible(true);
        modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);

        field.set(null, newValue);
    }
}
