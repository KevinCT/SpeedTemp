package com.zweigbergk.speedswede.database;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.zweigbergk.speedswede.Constants;
import com.zweigbergk.speedswede.core.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public enum LocalStorage {
    INSTANCE;

    public static final String TAG = LocalStorage.class.getSimpleName().toUpperCase();
    private static List<String>  keyList= new ArrayList();
    private static List<String>  valueList= new ArrayList();

    public void saveSettings(Context context, String key, String value){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        keyList.add(key);
        valueList.add(value);
        if(keyList!=null && valueList.size()==keyList.size()){
            for(int i=0;i<keyList.size();i++){
                editor.putString(keyList.get(i),valueList.get(i));
            }
        }
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

    public void removeSettings(Context context){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        preferences.edit().clear().commit();
    }

}
