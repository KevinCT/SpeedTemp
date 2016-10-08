package com.zweigbergk.speedswede;

import android.util.Log;

import com.zweigbergk.speedswede.core.ChatMatcher;
import com.zweigbergk.speedswede.core.User;
import com.zweigbergk.speedswede.database.DatabaseHandler;
import com.zweigbergk.speedswede.core.User.Preference;
import com.zweigbergk.speedswede.database.UserReference;
import com.zweigbergk.speedswede.database.DatabaseHandler.DatabaseNode;
import com.zweigbergk.speedswede.util.Statement;

import static com.zweigbergk.speedswede.util.Statement.not;

import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Date;

public class Initializer {

    public static final String TAG = Initializer.class.getSimpleName().toUpperCase();

    public static void onLogin() {
        DatabaseHandler.onStartup();

        addUserToDatabase();

        DatabaseHandler.registerListener(DatabaseNode.CHATS);
        DatabaseHandler.registerListener(DatabaseNode.USERS);
        DatabaseHandler.getPool().bind(ChatMatcher.INSTANCE::handleUser);

        SimpleDateFormat f = new SimpleDateFormat("EE MMM dd", Locale.getDefault());
        Log.e("DATE", f.format(new Date()));
    }

    private static void addUserToDatabase() {
        User activeUser = DatabaseHandler.getActiveUser();
        Statement containsUser = DatabaseHandler.hasUser(activeUser);

        not(containsUser).then(() -> {
            DatabaseHandler.users().push(activeUser);

            UserReference userRef = DatabaseHandler.get(activeUser);

            userRef.setPreference(Preference.LANGUAGE, Constants.ENGLISH);
            userRef.setPreference(Preference.NOTIFICATIONS, true);
            userRef.setPreference(Preference.SWEDISH_SKILL, 9999);
        });
    }
}
