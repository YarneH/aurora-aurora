package com.aurora.internalservice.internalprocessor.pdfparsing;

import java.util.HashMap;
import java.util.Map;

/**
 * Convert tags extracted from a Tagged PDF to tags supported by the {@link PDFContentExtractor}
 */
public class TagConverter {

    /**
     * These are the main tags the extractor can use
     */
    public static final String MAIN_SUPPORTED_TAGS = "(H[0-9]+|P|Figure)";

    /**
     * Mapping of extracted tags to supported tags
     */
    private static Map<String, String> tagConverter = TagConverter.fillTagConverter();

    /**
     * Converts a tag extracted from a PDF  to a {@link #MAIN_SUPPORTED_TAGS}
     * @param tag the extracted tag
     * @return the possibly accepted tag
     */
    public static String convertTag(String tag){
        if (tagConverter.containsKey(tag)){
            tag = tagConverter.get(tag);
        }
        return tag;
    }

    private static Map<String,String> fillTagConverter(){
        Map<String, String> tagConverter = new HashMap<>();
        tagConverter.put("Text body", "P");
        return tagConverter;
    }
}
