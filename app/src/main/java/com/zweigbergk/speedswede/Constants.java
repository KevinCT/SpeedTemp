package com.zweigbergk.speedswede;

import android.util.Log;

import com.zweigbergk.speedswede.util.Lists;

import java.util.Arrays;
import java.util.Locale;

public class Constants {
    public static final String ERROR = "ERROR";
    public static final String DEBUG = "DEBUG";

    public static final String CHATS = "chats";
    public static final String USERS = "users";
    public static final String USER_TO_CHAT = "user_chat";
    public static final String MESSAGES = "messages";
    public static final String TIMESTAMP = "timeStamp";
    public static final String FIRST_USER = "firstUser";
    public static final String POOL = "pool";
    public static final String SECOND_USER = "secondUser";
    public static final String NAME = "name";
    public static final String DISPLAY_NAME = "displayName";
    public static final String UNDEFINED = "undefined";
    public static final String PREFERENCES = "preferences";
    public static final String NOTIFICATIONS = "notifications";
    public static final String LANGUAGE = "language";
    public static final String SWEDISH_SKILL = "swedish_skill";
    public static final String STRANGER_SWEDISH_SKILL = "stranger_swedish_skill";

    //Languages
    public static final String ENGLISH = "en";
    public static final String SWEDISH = "sv";
    public static final String ARABIC = "ar";
    public static final String TURKISH = "tr";

    public static final String[] LANGUAGES = { ENGLISH, SWEDISH, ARABIC, TURKISH };


    public static final String USER_ID = "uid";

    public static final String TEST_USER_NAME = "Peter";
    public static final String TEST_USER_UID = "wS0GTtAOaRhztVGhaJYzFY4kQI82";

    public static String makePath(String... constants) {
        if (constants.length == 0)
            return "";

        StringBuilder builder = new StringBuilder();
        Lists.forEach(Arrays.asList(constants),c -> {
            builder.append(c);
            builder.append('/');
        });

        builder.deleteCharAt(builder.length() - 1);

        return builder.toString();
    }

    public static String preference(String preference) {
        return makePath(PREFERENCES, preference);
    }
}
