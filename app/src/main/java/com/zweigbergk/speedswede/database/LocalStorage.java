package com.zweigbergk.speedswede.database;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.zweigbergk.speedswede.activity.LoginActivity;
import com.zweigbergk.speedswede.core.User;
import com.zweigbergk.speedswede.core.UserProfile;

public enum LocalStorage {
    INSTANCE;

    public static final String TAG = LocalStorage.class.getSimpleName().toUpperCase();

    public static final String LANGUAGE = "language";

    public void saveSettings(Context context, String language){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(LANGUAGE, language);
        editor.apply();
    }

    public String getLanguage(Context context, String deviceLanguage){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String language = preferences.getString(LANGUAGE, deviceLanguage);
        return language;
    }

}
