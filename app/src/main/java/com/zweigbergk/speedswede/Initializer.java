package com.zweigbergk.speedswede;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.zweigbergk.speedswede.activity.Language;
import com.zweigbergk.speedswede.core.ChatMatcher;
import com.zweigbergk.speedswede.core.SkillCategory;
import com.zweigbergk.speedswede.core.User;
import com.zweigbergk.speedswede.core.UserProfile;
import com.zweigbergk.speedswede.database.DatabaseHandler;
import com.zweigbergk.speedswede.database.DatabaseHandler.DatabaseNode;
import com.zweigbergk.speedswede.util.Stringify;
import com.zweigbergk.speedswede.util.collection.ArrayList;
import com.zweigbergk.speedswede.util.collection.List;
import com.zweigbergk.speedswede.util.methodwrapper.Client;
import com.zweigbergk.speedswede.util.methodwrapper.Executable;

import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Date;

public class Initializer {

    public static final String TAG = Initializer.class.getSimpleName().toUpperCase();

    private static List<Executable> loginExecutables = new ArrayList<>();
    private static List<Client<User>> loginClients = new ArrayList<>();

    public static void onLogin(boolean isOfflineMode) {
        DatabaseHandler.onStartup();

        if (!isOfflineMode) {
            onlineLogin();
        } else {
            offlineLogin();
        }

        DatabaseHandler.registerListener(DatabaseNode.CHATS);
        DatabaseHandler.registerListener(DatabaseNode.USERS);
        DatabaseHandler.getPool().bind(ChatMatcher.INSTANCE::handleUser);
    }

    private static void onlineLogin() {
        Log.d(TAG, "onlineLogin()");
        User userShell = UserProfile.from(FirebaseAuth.getInstance().getCurrentUser());

        DatabaseHandler.getReference(userShell).pull().then(user -> {
            if (user != null) {
                Log.d(TAG, "We already had that one :) ID: " + userShell.getUid());
                DatabaseHandler.setLoggedInUser(user);

            } else {
                Log.d(TAG, "That's a new user! Pushing user with id: " + userShell.getUid());
                DatabaseHandler.users().push(userShell);

                //Set default preferences
                DatabaseHandler.getReference(userShell).setPreference(User.Preference.SKILL_CATEGORY, SkillCategory.MENTOR.toString());
                DatabaseHandler.getReference(userShell).setPreference(User.Preference.NOTIFICATIONS, true);
                DatabaseHandler.getReference(userShell).setPreference(User.Preference.LANGUAGE, Language.SWEDISH.toString());
            }

            notifyListeners(user);
        });
    }

    private static void offlineLogin() {
        Log.d(TAG, "offlineLogin()");

        //Get object with User name and id
        UserProfile userShell = UserProfile.from(FirebaseAuth.getInstance().getCurrentUser());

        //Get the actual user
        DatabaseHandler.getReference(userShell).pull().then(user -> {
            if (user != null) {
                DatabaseHandler.setLoggedInUser(user);
            } else {
                DatabaseHandler.setLoggedInUser(userShell);
            }

            notifyListeners(user);
        });
    }

    private static void notifyListeners(User user) {
        Log.d(TAG, Stringify.curlyFormat("Our user firstLogin: {firstlogin}", user.isFirstLogin()));
        loginExecutables.foreach(Executable::run);
        loginExecutables = new ArrayList<>();

        loginClients.foreach(client -> client.supply(user));
    }

    public static void runOnLogin(Executable executable) {
        loginExecutables.add(executable);
    }

    public static void runOnLogin(Client<User> client) {
        loginClients.add(client);
    }
}