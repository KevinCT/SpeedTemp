package com.zweigbergk.speedswede;

import android.util.Log;

import com.zweigbergk.speedswede.activity.Language;
import com.zweigbergk.speedswede.core.ChatMatcher;
import com.zweigbergk.speedswede.core.SkillCategory;
import com.zweigbergk.speedswede.core.User;
import com.zweigbergk.speedswede.database.DatabaseHandler;
import com.zweigbergk.speedswede.database.DatabaseHandler.DatabaseNode;
import com.zweigbergk.speedswede.util.PreferenceValue;
import com.zweigbergk.speedswede.util.Stringify;
import com.zweigbergk.speedswede.util.async.Statement;

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

        containsUser.onFalse(() -> {
            Log.d(TAG, "Pushing " + activeUser.getUid());
            DatabaseHandler.users().push(activeUser);

            DatabaseHandler.get(activeUser).setPreference(User.Preference.SKILL_CATEGORY, SkillCategory.MENTOR.toString());
            DatabaseHandler.get(activeUser).setPreference(User.Preference.NOTIFICATIONS, true);
            DatabaseHandler.get(activeUser).setPreference(User.Preference.LANGUAGE, Language.SWEDISH.toString());
        });

        DatabaseHandler.get(activeUser).pull().then(user -> {
            SkillCategory category = user.getSkillCategory();
            Language lang = user.getLanguage();
            boolean notify = user.getNotificationPreference();
            Log.d(TAG, Stringify.curlyFormat("category: {0}, lang: {1}, notify: {2}", category, lang, notify));
        });
    }
}
