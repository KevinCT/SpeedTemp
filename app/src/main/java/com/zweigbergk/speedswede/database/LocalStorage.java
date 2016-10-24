package com.zweigbergk.speedswede.database;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.zweigbergk.speedswede.Constants;

import java.util.Locale;

public enum LocalStorage {
    INSTANCE;

    public static final String TAG = LocalStorage.class.getSimpleName().toUpperCase();

    public void saveLanguage(Context context, String value){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(Constants.LANGUAGE, value);
        editor.apply();
    }

    public String getLanguage(Context context){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(Constants.LANGUAGE, Locale.getDefault().getLanguage());
    }

    public String getString(Context context, String key, String defaultValue){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(key,defaultValue);
    }

    public void removeSetting(Context context, String key){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove(key);
        editor.apply();


    }

}
