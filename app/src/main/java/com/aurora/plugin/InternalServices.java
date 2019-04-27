package com.aurora.plugin;

/**
 * Enum for the different internal services that the plugins can make use of.
 * NOTE: for now there is only text extraction, but when new services are added like OCR and parsing,
 * these services should be added to this enum
 */
public enum InternalServices {
    TEXT_EXTRACTION, TRANSLATION,
    IMAGE_EXTRACTION,
    NLP_TOKENIZE,
    NLP_SSPLIT,
    NLP_POS
}
