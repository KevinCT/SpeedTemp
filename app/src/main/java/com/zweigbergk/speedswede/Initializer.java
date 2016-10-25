package com.zweigbergk.speedswede;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.zweigbergk.speedswede.activity.Language;
import com.zweigbergk.speedswede.core.ChatMatcher;
import com.zweigbergk.speedswede.core.SkillCategory;
import com.zweigbergk.speedswede.core.User;
import com.zweigbergk.speedswede.core.UserProfile;
import com.zweigbergk.speedswede.database.DatabaseHandler;
import com.zweigbergk.speedswede.util.collection.ArrayListExtension;
import com.zweigbergk.speedswede.util.collection.ListExtension;
import com.zweigbergk.speedswede.util.methodwrapper.Client;
import com.zweigbergk.speedswede.util.methodwrapper.Executable;

public class Initializer {
    private static final String TAG = Initializer.class.getSimpleName().toUpperCase();

    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    private static ListExtension<Executable> loginExecutables = new ArrayListExtension<>();
    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    private static ListExtension<Client<User>> loginClients = new ArrayListExtension<>();

    public static void onLogin(boolean isOfflineMode) {
        if (!isOfflineMode) {
            onlineLogin();
        } else {
            offlineLogin();
        }

        DatabaseHandler.registerUsersListener();
        DatabaseHandler.getPool().bind(ChatMatcher.INSTANCE::handleUser);
    }

    private static void onlineLogin() {
        Log.d(TAG, "onlineLogin()");
        User userShell = UserProfile.from(FirebaseAuth.getInstance().getCurrentUser());

        DatabaseHandler.getReference(userShell).pull().then(user -> {
            if (user != null) {
                Log.d(TAG, "We already had that one :) ID: " + userShell.getUid());
            } else {
                Log.d(TAG, "That's a new user! Pushing user with id: " + userShell.getUid());
                user = userShell;
                DatabaseHandler.users().push(userShell);

                //SetExtension default preferences
                DatabaseHandler.getReference(userShell).setPreference(User.Preference.SKILL_CATEGORY, SkillCategory.MENTOR.toString());
                DatabaseHandler.getReference(userShell).setNotifications(true);
                DatabaseHandler.getReference(userShell).setPreference(User.Preference.LANGUAGE, Language.SWEDISH.toString());
            }

            DatabaseHandler.setLoggedInUser(user);
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
        loginExecutables.foreach(Executable::run);
        loginExecutables = new ArrayListExtension<>();

        loginClients.foreach(client -> client.supply(user));
    }

    public static void runOnLogin(Client<User> client) {
        loginClients.add(client);
    }
}