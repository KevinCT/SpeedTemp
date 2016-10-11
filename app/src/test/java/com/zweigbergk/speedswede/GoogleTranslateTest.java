package com.zweigbergk.speedswede;

import android.util.Log;

import org.junit.Test;
import org.junit.Before;
import org.junit.Test;

// Imports the Google Cloud client library
import com.google.cloud.translate.Translate;
import com.google.cloud.translate.Translate.TranslateOption;
import com.google.cloud.translate.TranslateOptions;
import com.google.cloud.translate.Translation;

import static org.junit.Assert.assertTrue;

public class GoogleTranslateTest {
    private final String API_KEY = "AIzaSyCjL04iIPrLYwqCVyCrIvRWwMA60yeMSvE";



    @Test
    public void testGoogleTranslate() {

        Translate translate = TranslateOptions.builder().apiKey(API_KEY).build().service();

        // The text to translate
        String text = "Hej världen nu är det snart matte!";

        // Translates some text into Russian
        Translation translation = translate.translate(
                text,
                TranslateOption.sourceLanguage("sv"),
                TranslateOption.targetLanguage("fr")
        );

        System.out.printf("Text: %s%n", text);
        System.out.printf("Translation: %s%n", translation.translatedText());
    }

}
