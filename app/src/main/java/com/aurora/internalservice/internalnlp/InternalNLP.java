package com.aurora.internalservice.internalnlp;

import com.aurora.internalservice.InternalService;

import java.util.ArrayList;
import java.util.List;

import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.AnnotationPipeline;
import edu.stanford.nlp.pipeline.MorphaAnnotator;
import edu.stanford.nlp.pipeline.ParserAnnotator;
import edu.stanford.nlp.pipeline.TokenizerAnnotator;
import edu.stanford.nlp.pipeline.WordsToSentencesAnnotator;

public class InternalNLP implements InternalService {

    /**
     * A constant needed for the creation of the parser
     */
    private static final int MAX_SENTENCES_FOR_PARSER = 100;

    private AnnotationPipeline mAnnotationPipeline;

    /**
     * Default constructor
     */
    public InternalNLP() {
        mAnnotationPipeline = buildPipeline();
    }

    /**
     *
     * @return
     */
    private AnnotationPipeline buildPipeline() {
        AnnotationPipeline pipeline = new AnnotationPipeline();
        pipeline.addAnnotator(new TokenizerAnnotator(false, "en"));
        pipeline.addAnnotator(new WordsToSentencesAnnotator(false));

        pipeline.addAnnotator(new ParserAnnotator(false, MAX_SENTENCES_FOR_PARSER));
        pipeline.addAnnotator(new MorphaAnnotator(false));
        return pipeline;
    }

    /**
     *
     * @param annotation coreNLP annotation that needs to be annotated
     */
    public void annotate(Annotation annotation) {
        this.mAnnotationPipeline.annotate(annotation);
    }

}
