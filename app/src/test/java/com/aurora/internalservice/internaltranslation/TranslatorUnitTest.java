package com.aurora.internalservice.internaltranslation;

import com.aurora.kernel.event.TranslationResponse;
import com.aurora.util.FakeRequestQueue;

import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import static org.junit.Assert.assertEquals;

public class TranslatorUnitTest {

    @Test
    public void Translator_makeUrl_CorrectURLIsMade() throws UnsupportedEncodingException {
        String[] trans = {"this is a test", "what a cool test", "with escape characters / + . ", "how does that translate"};
        String response = Translator.makeUrl(trans, "en", "nl");
        String answer = "https://translation.googleapis.com/language/translate/v2?q=this+is+a+test&q=what+a+cool+test&q=with+escape+characters+%2F+%2B+.+&q=how+does+that+translate&target=nl&source=en&key=AIzaSyBT0I7M_hZlTmpD1hqC8beY1ILzpWGSU4s";
        assertEquals("they are not equal", answer, response);
    }


}