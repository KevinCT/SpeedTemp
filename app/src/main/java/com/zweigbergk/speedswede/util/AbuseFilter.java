package com.zweigbergk.speedswede.util;

import com.zweigbergk.speedswede.util.collection.Arrays;
import com.zweigbergk.speedswede.util.collection.ListExtension;

import java.util.Locale;

public class AbuseFilter {

    private static String[] forbiddenWords = {
            "fuck", "fucker", "fucking"
    };

    public static String filterMessage(String input) {
        /*String[] inputWords = input.split("\\s+");
        StringBuilder outputWords = new StringBuilder();

        for (int i = 0; i < inputWords.length; i++) {
            for (int j = 0; j < forbiddenWords.length; j++) {
                if ((inputWords[i].toLowerCase()).equals(forbiddenWords[j])) {
                    inputWords[i] = "***";
                    break;
                }
            }


            outputWords.append(inputWords[i]);
            if ((i + 1) < inputWords.length) {
                outputWords.append(" ");
            }
        }*/

        StringBuilder result = new StringBuilder();
        ListExtension<String> forbidden = Arrays.asList(forbiddenWords);
        Arrays.asList(input.split("\\s+")).map(word ->
                result.append((forbidden.contains(word.toLowerCase(Locale.ENGLISH)) ? "*** " : word + " ")));

        return result.toString();
    }
}