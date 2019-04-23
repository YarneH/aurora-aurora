package com.aurora.internalservice.internalnlp;

import android.util.Log;

import com.aurora.auroralib.ExtractedText;
import com.aurora.auroralib.Section;
import com.aurora.internalservice.InternalService;
import com.aurora.plugin.InternalServices;

import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.AnnotationPipeline;
import edu.stanford.nlp.pipeline.POSTaggerAnnotator;
import edu.stanford.nlp.pipeline.ProtobufAnnotationSerializer;
import edu.stanford.nlp.pipeline.TokenizerAnnotator;
import edu.stanford.nlp.pipeline.WordsToSentencesAnnotator;

/**
 * Class responsible for the CoreNLP service provided by Aurora. The currently supported CoreNLP
 * annotators are Tokenize, Ssplit and Pos.
 */
public class InternalNLP implements InternalService {

    /** The CoreNLP annotation pipeline */
    private AnnotationPipeline mAnnotationPipeline;

    /** Serializer used for to serialize the annotations. Uses Google's protobuf scheme */
    private ProtobufAnnotationSerializer mAnnotationSerializer;

    /**
     * Default constructor
     */
    public InternalNLP() {
        mAnnotationPipeline = new AnnotationPipeline();
        mAnnotationSerializer = new ProtobufAnnotationSerializer(true);
    }

    /**
     * Method that adds an Annotator to the AnnotationPipeline if it is supported
     *
     * @param annotator Element from the InternalService Enum, should start with NLP_
     */
    public void addAnnotator(InternalServices annotator) {

        try {
            switch (annotator) {
                case NLP_TOKENIZE:
                    mAnnotationPipeline.addAnnotator(new TokenizerAnnotator(false, "en"));
                    break;
                case NLP_SSPLIT:
                    mAnnotationPipeline.addAnnotator(new WordsToSentencesAnnotator(false));
                    break;
                case NLP_POS:
                    mAnnotationPipeline.addAnnotator(new POSTaggerAnnotator(false));
                    break;

                default:
                    Log.d("NLP", annotator.name() + " Is currently not yet supported");

            }
        } catch (Exception e) {
            Log.e("NLP", "Creating the annotation pipeline failed", e);
        }
    }

    /**
     * Add annotations to all text in the extractedText object. This is the Title, the Section
     * titles and the Section body
     *
     * @param extractedText ExtractedText object that should be annotated
     */
    public void annotate(ExtractedText extractedText) {

        // Title annotations
        if (extractedText.getTitle() != null) {
            Annotation annotatedTitle = new Annotation(extractedText.getTitle());
            mAnnotationPipeline.annotate(annotatedTitle);
            extractedText.setTitleAnnotations(mAnnotationSerializer.toProto(annotatedTitle));
        }

        // Section annotations
        for (Section section: extractedText.getSections()) {
            // Section title annotations
            if (section.getTitle() != null) {
                Annotation annotatedTitle = new Annotation(section.getTitle());
                mAnnotationPipeline.annotate(annotatedTitle);
                section.setTitleAnnotations(mAnnotationSerializer.toProto(annotatedTitle));
            }

            // Section body annotations
            if (section.getBody() != null) {
                Annotation annotatedBody = new Annotation(section.getBody());
                mAnnotationPipeline.annotate(annotatedBody);
                section.setBodyAnnotations(mAnnotationSerializer.toProto(annotatedBody));
            }
        }
    }
}
