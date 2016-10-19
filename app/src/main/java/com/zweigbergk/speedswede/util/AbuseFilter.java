package com.zweigbergk.speedswede.util;

public class AbuseFilter {

    private static String[] forbiddenWords = {
            "fuck", "fucker", "fucking"
    };

    public static String filterMessage(String input) {
        String[] inputWords = input.split("\\s+");
        StringBuilder outputWords = new StringBuilder();

        for (int i = 0; i < inputWords.length; i++) {
            for (int j = 0; j < forbiddenWords.length; j++) {
                if ((inputWords[i].toLowerCase()).equals(forbiddenWords[j])) {
                    inputWords[i] = "***";
                    break;
                }
            }


            outputWords.append(inputWords[i]);
            if ((i+1) < inputWords.length) {
                outputWords.append(" ");
            }
        }


        return outputWords.toString();
    }
}
