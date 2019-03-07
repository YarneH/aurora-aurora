package com.aurora.plugin;

import java.util.List;

/**
 * Basic class for representing processed text (can be instantiated).
 */
public class BasicProcessedText extends ProcessedText {
    public BasicProcessedText(String title, List<String> paragraphs) {
        super(title, paragraphs);
    }
}
