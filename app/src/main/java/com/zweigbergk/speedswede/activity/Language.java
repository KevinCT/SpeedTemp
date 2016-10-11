package com.zweigbergk.speedswede.activity;

public enum Language {
    ENGLISH("en"), SWEDISH("sv");

    private final String mLanguageCode;

    Language(String languageCode) {
        mLanguageCode = languageCode;
    }

    public String getLanguageCode() {
        return mLanguageCode;
    }
}
