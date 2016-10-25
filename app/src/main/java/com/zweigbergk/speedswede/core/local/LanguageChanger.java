package com.zweigbergk.speedswede.core.local;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;

import com.zweigbergk.speedswede.Constants;
import com.zweigbergk.speedswede.database.LocalStorage;

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
        String newLanguage = languageCode.length() != 0 ? languageCode : Constants.ENGLISH;
        config.setLocale(new Locale(newLanguage));

        resources.updateConfiguration(config, resources.getDisplayMetrics());
        saveLanguage(context, newLanguage);
    }

    private static void saveLanguage(Context context, String language){
        LocalStorage.INSTANCE.saveLanguage(context, language);
    }

    public static Locale getCurrentLocale(Context context) {
        Resources resources = context.getResources();
        if (Build.VERSION.SDK_INT >= 24) {
            return resources.getConfiguration().getLocales()
                    .getFirstMatch(resources.getAssets().getLocales());
        } else {
            //noinspection deprecation
            return resources.getConfiguration().locale;
        }
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
