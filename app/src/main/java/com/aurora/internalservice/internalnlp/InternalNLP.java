package com.aurora.internalservice.internalnlp;

import android.util.Log;

import com.aurora.auroralib.ExtractedText;
import com.aurora.auroralib.Section;
import com.aurora.internalservice.InternalService;
import com.aurora.plugin.InternalServices;

import java.util.Properties;

import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.AnnotationPipeline;
import edu.stanford.nlp.pipeline.POSTaggerAnnotator;
import edu.stanford.nlp.pipeline.ProtobufAnnotationSerializer;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.pipeline.TokenizerAnnotator;
import edu.stanford.nlp.pipeline.WordsToSentencesAnnotator;

public class InternalNLP implements InternalService {

    /** The CoreNLP annotation pipeline */
    private AnnotationPipeline mAnnotationPipeline;

    /** */
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
     * Add annotations to all text in the extractedText object
     *
     * @param extractedText ExtractedText object that should be annotated
     */
    public void annotate(ExtractedText extractedText) {

        if (extractedText.getTitle() != null) {
            Annotation annotatedTitle = new Annotation(extractedText.getTitle());

            mAnnotationPipeline.annotate(annotatedTitle);

            extractedText.setTitleAnnotations(mAnnotationSerializer.toProto(annotatedTitle));

            Log.d("NLP", mAnnotationSerializer.toProto(annotatedTitle).toString());
        }

    }



    private AnnotationPipeline buildPipeline() {
/*        AnnotationPipeline pipeline = new AnnotationPipeline();
        pipeline.addAnnotator(new TokenizerAnnotator(false, "en"));
        pipeline.addAnnotator(new WordsToSentencesAnnotator(false));
        new ParserAnnotator()
        pipeline.addAnnotator(new ParserAnnotator(false, MAX_SENTENCES_FOR_PARSER));
        //pipeline.addAnnotator(new MorphaAnnotator(false));
        return pipeline;*/



        Properties props = new Properties();
        props.setProperty("annotators", "tokenize,ssplit,pos,lemma,ner,parse,depparse,coref,kbp,quote");
        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
        return pipeline;
    }

}
