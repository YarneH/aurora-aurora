package com.aurora.internalservice.internalnlp;

import android.util.Log;

import com.aurora.auroralib.ExtractedText;
import com.aurora.auroralib.Section;
import com.aurora.internalservice.InternalService;
import com.aurora.plugin.InternalServices;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.AnnotationPipeline;
import edu.stanford.nlp.pipeline.Annotator;
import edu.stanford.nlp.pipeline.POSTaggerAnnotator;
import edu.stanford.nlp.pipeline.ProtobufAnnotationSerializer;
import edu.stanford.nlp.pipeline.TokenizerAnnotator;
import edu.stanford.nlp.pipeline.WordsToSentencesAnnotator;

/**
 * Class responsible for the CoreNLP service provided by Aurora. The currently supported CoreNLP
 * annotators are Tokenize, Ssplit and Pos.
 */
public class InternalNLP implements InternalService {

    /** Tag for logging purposes */
    private static final String CLASS_TAG = "InternalNLP";

    /** Id of the tokenize annotator in the basicAnnotators */
    private static final int TOKENIZE = 0;

    /** Id of the ssplit annotator in the basicAnnotators */
    private static final int SSPLIT = 1;

    /** Id of the pos annotator in the basicAnnotators */
    private static final int POS = 2;

    /** Static list of 3 default annotators that will only be loaded at most once */
    private static final List<Annotator> sBasicAnnotators = new ArrayList<>();

    /** The CoreNLP annotation pipeline */
    private AnnotationPipeline mAnnotationPipeline;

    /** Serializer used for to serialize the annotations. Uses Google's protobuf scheme */
    private ProtobufAnnotationSerializer mAnnotationSerializer;

    /** Set of satisfied dependencies */
    private Set<java.lang.Class<? extends edu.stanford.nlp.ling.CoreAnnotation>>
            mSatisfiedDependencies = new HashSet<>();

    static {
        synchronized (sBasicAnnotators) {
            sBasicAnnotators.add(new TokenizerAnnotator(false, "en"));
            sBasicAnnotators.add(new WordsToSentencesAnnotator(false));
            sBasicAnnotators.add(new POSTaggerAnnotator(false));

            sBasicAnnotators.notifyAll();
        }
    }

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
                    addAnnotatorIfSatisfied(sBasicAnnotators.get(TOKENIZE), annotator);
                    break;
                case NLP_SSPLIT:
                    addAnnotatorIfSatisfied(sBasicAnnotators.get(SSPLIT), annotator);
                    break;
                case NLP_POS:
                    addAnnotatorIfSatisfied(sBasicAnnotators.get(POS), annotator);
                    break;

                default:
                    Log.d(CLASS_TAG, annotator.name() + " Is currently not yet supported");

            }
        } catch (Exception e) {
            Log.e(CLASS_TAG, "Creating the annotation pipeline failed", e);
        }
    }

    /**
     * Private helper method that adds the Annotator if its dependencies are satisfied
     *
     * @param annotator     Annotator that needs to be added to the pipeline
     * @param annotatorName Name of the Annotator for logging purposes
     */
    private void addAnnotatorIfSatisfied(Annotator annotator, InternalServices annotatorName) {
        if (dependenciesSatisfied(annotator, annotatorName)) {
            mAnnotationPipeline.addAnnotator(annotator);
            mSatisfiedDependencies.addAll(annotator.requirementsSatisfied());
            Log.d(CLASS_TAG, annotatorName.name() + " has been added to the NLP pipeline");
        }
    }

    /**
     * Private helper method that checks if all the dependencies of the Annotator are satisfied.
     *
     * @param annotator     Annotator for which the dependencies will be checked.
     * @param annotatorName Name of the Annotator for logging purposes
     * @return True if all dependencies are satisfied, false otherwise.
     */
    // Sonar says "Set<Class>" cannot contain a "Class", but this is incorrect
    @java.lang.SuppressWarnings("squid:S2175")
    private boolean dependenciesSatisfied(Annotator annotator, InternalServices annotatorName) {
        boolean allDependenciesSatisfied = true;

        for (java.lang.Class<? extends edu.stanford.nlp.ling.CoreAnnotation> dependency :
                annotator.requires()) {
            if (!mSatisfiedDependencies.contains(dependency)) {
                Log.e(CLASS_TAG, "Dependency " + dependency.getSimpleName() + " required by " +
                        annotatorName + " is not yet satisfied. " +
                        annotatorName + " will not be added to the pipeline");
                allDependenciesSatisfied = false;
            }
        }
        return allDependenciesSatisfied;
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
            extractedText.setTitleAnnotationProto(mAnnotationSerializer.toProto(annotatedTitle));
        }

        // Section annotations
        for (Section section: extractedText.getSections()) {
            // Section title annotations
            if (section.getTitle() != null) {
                Annotation annotatedTitle = new Annotation(section.getTitle());
                mAnnotationPipeline.annotate(annotatedTitle);
                section.setTitleAnnotationProto(mAnnotationSerializer.toProto(annotatedTitle));
            }

            // Section body annotations
            if (section.getBody() != null) {
                Annotation annotatedBody = new Annotation(section.getBody());
                mAnnotationPipeline.annotate(annotatedBody);
                section.setBodyAnnotationProto(mAnnotationSerializer.toProto(annotatedBody));
            }
        }
    }
}
