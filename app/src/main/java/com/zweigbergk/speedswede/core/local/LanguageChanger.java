package com.zweigbergk.speedswede.core.local;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.Log;

import com.zweigbergk.speedswede.Constants;
import com.zweigbergk.speedswede.database.LocalStorage;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class LanguageChanger {
    private static boolean mChanged = false;

    public static void onCreate(Context context){
        String language = LocalStorage.INSTANCE.getLanguage(context, Locale.getDefault().getLanguage());
        saveLanguage(context, language);
        changeLanguage(language, context);

    }
    public static void changeLanguage(String language, Context context){
        Resources resources = context.getResources();
        Configuration config;
        config = resources.getConfiguration();

        List<String> languages = Arrays.asList(Constants.LANGUAGES);
        language = languages.contains(language) ? language : Constants.ENGLISH;

        resources.updateConfiguration(config, resources.getDisplayMetrics());
        saveLanguage(context,language);
    }

    private static void saveLanguage(Context context, String language){
        LocalStorage.INSTANCE.saveSettings(context, language);
    }

    public static void languageChanged(Boolean changed){
        mChanged = changed;
    }

    public static boolean isChanged(){
        return mChanged;
    }

}
