package com.zweigbergk.speedswede.core.local;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.Log;

import com.zweigbergk.speedswede.Constants;
import com.zweigbergk.speedswede.database.LocalStorage;

import java.util.Arrays;
import com.zweigbergk.speedswede.util.collection.List;
import java.util.Locale;

public class LanguageChanger {
    private static boolean mChanged = false;

    public static void onCreate(Context context){
        String language = LocalStorage.INSTANCE.getLanguage(context);
        saveLanguage(context, language);
        changeLanguage(language, context);

    }
    public static void changeLanguage(String languageCode, Context context){
        Resources resources = context.getResources();
        Configuration config;
        config = resources.getConfiguration();

        languageCode = languageCode.contains(languageCode) ? languageCode : Constants.ENGLISH;
        config.locale = new Locale(languageCode);

        resources.updateConfiguration(config, resources.getDisplayMetrics());
        saveLanguage(context,languageCode);
    }

    private static void saveLanguage(Context context, String language){
        LocalStorage.INSTANCE.saveSettings(context, Constants.LANGUAGE, language);
    }

    public static Locale getCurrentLocale(Context context) {
        return context.getResources().getConfiguration().locale;
    }

    public static void languageChanged(Boolean changed){
        mChanged = changed;
    }

    public static String getCurrentLanguage(Context context) {
        return LocalStorage.INSTANCE.getLanguage(context);
    }

    public static boolean isChanged(){
        return mChanged;
    }

}
