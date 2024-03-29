package com.aurora.internalservice.internalprocessor.pdfparsing;

import java.util.HashMap;
import java.util.Map;

/**
 * Convert tags extracted from a Tagged PDF to tags supported by the {@link PDFContentExtractor}
 */
public final class TagConverter {

    /**
     * These are the main tags the extractor can use
     */
    public static final String MAIN_SUPPORTED_TAGS = "(H[0-9]+|P|Figure)";

    /**
     * Mapping of extracted tags to supported tags
     */
    private static Map<String, String> tagConverterMap = TagConverter.fillTagConverter();

    private TagConverter(){
        // Empty because utility class
    }
    /**
     * Converts a tag extracted from a PDF  to a {@link #MAIN_SUPPORTED_TAGS}
     * @param tag the extracted tag
     * @return the possibly accepted tag
     */
    static String convertTag(String tag){
        if (tagConverterMap.containsKey(tag)){
            tag = tagConverterMap.get(tag);
        }
        return tag;
    }


    /**
     * Fills the {@link #tagConverterMap} with entries
     * @return the translation to supported elements
     */
    private static Map<String,String> fillTagConverter(){
        Map<String, String> tagConverterMap = new HashMap<>();
        tagConverterMap.put("Text body", "P");
        return tagConverterMap;
    }
}
