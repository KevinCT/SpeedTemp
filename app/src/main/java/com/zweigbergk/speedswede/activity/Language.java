package com.zweigbergk.speedswede.activity;

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

//    public String[] getLanguageCodes() {
//        return new String[ENGLISH,]
//    }
}
