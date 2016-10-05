package com.zweigbergk.speedswede.core.local;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.Log;

import com.zweigbergk.speedswede.database.LocalStorage;

import java.util.Locale;

/**
 * Created by kevin on 04/10/2016.
 */

public class LanguageChanger {

    public static void onCreate(Context context){
        String language = LocalStorage.INSTANCE.getLanguage(context, Locale.getDefault().getLanguage());
        saveLanguage(context, language);
        changeLanguage(language, context);

    }
    public static void changeLanguage(String language, Context context){
        Resources resources = context.getResources();
        Configuration config;
        config = resources.getConfiguration();

        switch(language){
            case "sv":
                config.locale = new Locale("sv");
                break;
            default:
                config.locale = Locale.ENGLISH;
                break;
        }
        resources.updateConfiguration(config, resources.getDisplayMetrics());
        saveLanguage(context,language);
    }

    private static void saveLanguage(Context context, String language){
        LocalStorage.INSTANCE.saveSettings(context, language);
    }

}
