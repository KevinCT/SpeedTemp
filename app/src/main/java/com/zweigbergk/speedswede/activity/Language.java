package com.zweigbergk.speedswede.activity;

import com.zweigbergk.speedswede.util.collection.Collections;

public enum Language {
    ENGLISH("en"),
    SWEDISH("sv"),
    TURKISH("tu"),
    ARABIC("ar"),
    DARI("da");

    public static final Language DEFAULT = Language.ENGLISH;

    private final String mLanguageCode;

    Language(String languageCode) {
        mLanguageCode = languageCode;
    }


    public String toString() {
        return getLanguageCode();
    }
    public String getLanguageCode() {
        return mLanguageCode;
    }

    /**
     * @return the language matching the given languageCode, or null if there was no language for that code.
     */
    public static Language fromString(String languageCode) {
        return Collections.asList(values())
                .filter(language -> language.getLanguageCode().equalsIgnoreCase(languageCode))
                .getFirst();
    }
}
