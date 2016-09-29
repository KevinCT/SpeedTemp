package com.zweigbergk.speedswede.service;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.zweigbergk.speedswede.LoginActivity;
import com.zweigbergk.speedswede.core.Message;
import com.zweigbergk.speedswede.presenter.ChatPresenter;

public enum LocalStorage {
    INSTANCE;

    public static final String USER_ID = "user_state";

    public void saveActiveUserId(Context context) {
        if (DatabaseHandler.INSTANCE.getActiveUserId() != null) {
            SharedPreferences localState = PreferenceManager.getDefaultSharedPreferences(context);
            SharedPreferences.Editor editor = localState.edit();
            editor.putString(USER_ID, DatabaseHandler.INSTANCE.getActiveUserId());
            editor.apply();
        }
    }

    public void loadSavedUserId(LoginActivity activity) {
        SharedPreferences localState = PreferenceManager.getDefaultSharedPreferences(activity);
        String state = localState.getString(ChatPresenter.USER_ID, null);
        Log.d("DEBUG", state == null ? "null" : state);

        if (state != null) {
            Log.d("DEBUG", "Starting ChatActivity with old user session ID");
            activity.startChatActivity();
        } else {
            Toast.makeText(activity, "No previous user could be found.", Toast.LENGTH_SHORT).show();
        }
    }

}
