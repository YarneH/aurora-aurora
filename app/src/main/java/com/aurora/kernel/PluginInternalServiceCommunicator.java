package com.aurora.kernel;

import com.aurora.auroralib.ExtractedText;
import com.aurora.internalservice.internalprocessor.FileTypeNotSupportedException;
import com.aurora.internalservice.internalprocessor.InternalTextProcessor;
import com.aurora.kernel.event.InternalProcessorRequest;
import com.aurora.kernel.event.InternalProcessorResponse;

import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

import io.reactivex.Observable;

/**
 * Communicator that communicates with internal services offered to plugin processors
 */
public class PluginInternalServiceCommunicator extends Communicator {

    /**
     * internal text processor
     */
    private InternalTextProcessor mProcessor;

    /**
     * Observable keeping track of internal processor requests
     */
    private Observable<InternalProcessorRequest> internalProcessorEventObservable;

    public PluginInternalServiceCommunicator(Bus mBus, InternalTextProcessor processor) {
        super(mBus);
        mProcessor = processor;

        internalProcessorEventObservable = mBus.register(InternalProcessorRequest.class);
        internalProcessorEventObservable.subscribe((InternalProcessorRequest internalProcessorRequest) ->
                processFileWithInternalProcessor(internalProcessorRequest.getFile(),
                        internalProcessorRequest.getFileRef()));
    }

    private void processFileWithInternalProcessor(InputStream file, String fileRef) {
        // Call internal processor
        ExtractedText extractedText = null;
        try {
            extractedText = mProcessor.processFile(file, fileRef);
        } catch (FileTypeNotSupportedException e) {
            // TODO remove this (added for testing PluginIntegration while extractors not finished)
            List<String> paragraphs = Arrays.asList(
                    "Yield\n" +
                            "    6 servings\n" +
                            "Active Time\n" +
                            "    30 minutes\n" +
                            "Total Time\n" +
                            "    35 minutes\n" +
                            "\n" +
                            "Ingredients",

                    "        1 lb. linguine or other long pasta\n" +
                            "        Kosher salt\n" +
                            "        1 (14-oz.) can diced tomatoes\n" +
                            "        1/2 cup extra-virgin olive oil, divided\n" +
                            "        1/4 cup capers, drained\n" +
                            "        6 oil-packed anchovy fillets\n" +
                            "        1 Tbsp. tomato paste\n" +
                            "        1/3 cup pitted Kalamata olives, halved\n" +
                            "        2 tsp. dried oregano\n" +
                            "        1/2 tsp. crushed red pepper flakes\n" +
                            "        6 oz. oil-packed tuna",

                    "Preparation",

                    "        Cook pasta in a large pot of boiling salted water, stirring " +
                            "occasionally, until al dente. Drain pasta, reserving 1 cup pasta cooking " +
                            "liquid; return pasta to pot.\n" +
                            "        While pasta cooks, pour tomatoes into a fine-mesh sieve set over " +
                            "a medium bowl. Shake to release as much juice as possible, then let tomatoes " +
                            "drain in sieve, collecting juices in bowl, until ready to use.\n" +
                            "        Heat 1/4 cup oil in a large deep-sided skillet over medium-high. " +
                            "Add capers and cook, swirling pan occasionally, until they burst and are " +
                            "crisp, about 3 minutes. Using a slotted spoon, transfer capers to a paper " +
                            "towel-lined plate, reserving oil in skillet.\n" +
                            "        Combine anchovies, tomato paste, and drained tomatoes in skillet. " +
                            "Cook over medium-high heat, stirring occasionally, until tomatoes begin " +
                            "to caramelize and anchovies start to break down, about 5 minutes. Add " +
                            "collected tomato juices, olives, oregano, and red pepper flakes and bring " +
                            "to a simmer. Cook, stirring occasionally, until sauce is slightly thickened, " +
                            "about 5 minutes. Add pasta, remaining 1/4 cup oil, and 3/4 cup pasta " +
                            "cooking liquid to pan. Cook over medium heat, stirring and adding remaining " +
                            "1/4 cup pasta cooking liquid to loosen if needed, until sauce is thickened " +
                            "and emulsified, about 2 minutes. Flake tuna into pasta and toss to combine.\n" +
                            "        Divide pasta among plates. Top with fried capers.\n"

            );

            extractedText = new ExtractedText("ExtractedTextTitle", null);
            for (String section : paragraphs) {
                extractedText.addSimpleSection(section);
            }
            e.printStackTrace();
        }

        // Create response
        InternalProcessorResponse response = new InternalProcessorResponse(extractedText);

        // Post response
        mBus.post(response);
    }

}
