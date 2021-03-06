package com.zweigbergk.speedswede.util;

import android.util.Log;

import com.zweigbergk.speedswede.util.collection.Collections;

import java.util.Locale;

public class Stringify {
    private static final String TAG = Stringify.class.getSimpleName().toUpperCase(Locale.ENGLISH);

    /**
     * Format a String with args in curly braces. Use § sign for escaping characters.
     * @param source Unformatted string
     * @param args String arguments
     * @return A formatted string
     */
    public static String curlyFormat(String source, Object... args) {
        int i = 0;
        int argumentIndex = 0;
        boolean hold = false;

        StringBuilder builder = new StringBuilder();

        do {
            String currentChar = Character.toString(source.charAt(i));

            //If new arg found, halt building and insert arg
            if (currentChar.equals("{")) {
                hold = !hold;
                builder.append(args[argumentIndex++].toString());
                continue;
            }

            //Continue building
            if (currentChar.equals("}")) {
                hold = !hold;
                continue;
            }

            //Append characters like usual if we don't hold
            if (!hold) {
                builder.append(currentChar);
            }


        } while (++i < source.length());

        if (args.length != argumentIndex) {
            Log.w(TAG, "Stringify.curlyFormat: Expected " + args.length + " but only received " + (argumentIndex + 1) + " args.");
        }

        return builder.toString();
    }

    public static String removeCurlyBraces(String s) {
        return s.replace("{", "").replace("}", "");
    }

    @SuppressWarnings("unused")
    public static void printStackTrace() {
        StringBuilder builder = new StringBuilder();
        Collections.asList(Thread.currentThread().getStackTrace()).foreach(trace -> {
            if (trace.getLineNumber() >= 0) {
                builder.append(trace.getClassName());
                builder.append(Stringify.curlyFormat("(Line: {line})", trace.getLineNumber()));
                builder.append("\n");
            }
        });

        Log.d(TAG, builder.toString());
    }
}
