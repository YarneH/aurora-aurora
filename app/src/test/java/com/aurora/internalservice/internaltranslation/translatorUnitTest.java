package com.aurora.internalservice.internaltranslation;

import com.aurora.kernel.event.TranslationResponse;

import org.junit.Test;

import java.io.UnsupportedEncodingException;

import static org.junit.Assert.assertEquals;

public class translatorUnitTest {

    @Test
    public void t() throws UnsupportedEncodingException {
        String[] trans = {"this is a test", "what a cool test", "with escape characters / + . ", "how does that translate"};
        Translator translator = new Translator();
        String response = translator.makeUrl(trans, "en", "nl");
        String answer = "https://translation.googleapis.com/language/translate/v2?q=this+is+a+test&q=what+a+cool+test&q=with+escape+characters+%2F+%2B+.+&q=how+does+that+translate&target=nl&source=en&key={YOUR_API_KEY}";
        assertEquals("they are not equal", answer, response);
    }
}