package com.zweigbergk.speedswede;

import com.zweigbergk.speedswede.core.ChatMatcher;
import com.zweigbergk.speedswede.core.User;
import com.zweigbergk.speedswede.database.DatabaseHandler;
import com.zweigbergk.speedswede.core.User.Preference;
import com.zweigbergk.speedswede.database.UserReference;

import com.zweigbergk.speedswede.database.DatabaseHandler.DatabaseNode;

public class Initializer {

    public static void onLogin() {
        DatabaseHandler.getInstance().onStartup();

        addUserToDatabase();

        DatabaseHandler.getInstance().registerListener(DatabaseNode.CHATS);
        DatabaseHandler.getInstance().registerListener(DatabaseNode.USERS);
        DatabaseHandler.getPool().bind(ChatMatcher.INSTANCE::handleUser);
    }

    private static void addUserToDatabase() {
        User activeUser = DatabaseHandler.getInstance().getActiveUser();
        DatabaseHandler.users().push(activeUser);

        UserReference userRef = DatabaseHandler.get(activeUser);

        userRef.setPreference(Preference.LANGUAGE, Constants.ENGLISH);
        userRef.setPreference(Preference.NOTIFICATIONS, true);
        userRef.setPreference(Preference.SWEDISH_SKILL, 9999);
    }
}
