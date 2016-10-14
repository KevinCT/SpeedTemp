package com.zweigbergk.speedswede.activity;

import com.zweigbergk.speedswede.util.Lists;

import java.util.List;

public enum Language {
    ENGLISH("en"),
    SWEDISH("sv"),
    TURKISH("tu"),
    ARABIC("ar"),
    DARI("da");

    private final String mLanguageCode;

    Language(String languageCode) {
        mLanguageCode = languageCode;
    }

    public String getLanguageCode() {
        return mLanguageCode;
    }

    /**
     *
     * @param languageCode
     * @return the langauge matching the given languageCode, or null if there was no language for that code.
     */
    public static Language fromString(String languageCode) {
        List<Language> filtered = Lists.filter(values(), languageCode::equals);
        return filtered.size() != 0 ? filtered.get(0) : null;
    }
}
