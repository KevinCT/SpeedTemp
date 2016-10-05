package com.zweigbergk.speedswede.database;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.auth.AuthCredential;
import com.zweigbergk.speedswede.activity.LoginActivity;
import com.zweigbergk.speedswede.core.User;
import com.zweigbergk.speedswede.core.UserProfile;
import com.zweigbergk.speedswede.database.firebase.DbUserHandler;
import com.zweigbergk.speedswede.interactor.LoginInteractor;

public enum LocalStorage {
    INSTANCE;

    public static final String TAG = LocalStorage.class.getSimpleName().toUpperCase();

    public static final String USER_ID = "user_id";
    public static final String USER_NAME = "user_name";
    public static final String CREDENTIAL = "user_name";
    public static final String LANGUAGE = "language";

    public void saveActiveUser(Context context) {
        if (DbUserHandler.INSTANCE.getActiveUserId() != null) {
            SharedPreferences localState = PreferenceManager.getDefaultSharedPreferences(context);
            SharedPreferences.Editor editor = localState.edit();
            editor.putString(USER_ID, DbUserHandler.INSTANCE.getActiveUserId());
            editor.putString(USER_NAME, DbUserHandler.INSTANCE.getLoggedInUser().getDisplayName());
            //editor.put(CREDENTIAL, LoginInteractor.userCredential);
            editor.apply();
        }
    }

    public User getSavedUser(LoginActivity activity) {
        SharedPreferences localState = PreferenceManager.getDefaultSharedPreferences(activity);
        String id = localState.getString(USER_ID, null);
        String name = localState.getString(USER_NAME, null);
        Log.d(TAG, "Id of loaded user: " + (id == null ? "null" : id));
        Log.d(TAG, "Name of loaded user: " + (name == null ? "null" : name));

        return id == null ? null : new UserProfile(name, id);
    }

    public void saveSettings(Context context, String language){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(LANGUAGE, language);
        editor.apply();
    }

    public String getLanguage(Context context){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String language = preferences.getString(LANGUAGE,null);
        return language;
    }

}
