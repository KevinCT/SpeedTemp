package com.zweigbergk.speedswede.database;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.zweigbergk.speedswede.Constants;
import com.zweigbergk.speedswede.core.Chat;
import com.zweigbergk.speedswede.core.User;

import static com.zweigbergk.speedswede.database.UserReference.UserAttribute;

import java.util.Arrays;

public class Path {
    private static final DatabaseReference root = FirebaseDatabase.getInstance().getReference();

    public static final DatabaseReference CHATS = root.child(Constants.CHATS);
    public static final DatabaseReference USERS = root.child(Constants.USERS);

    public static DatabaseReference toChat(String uid) {
        return append(CHATS, uid);
    }

    public static DatabaseReference to(User user) {
        return append(USERS, user.getUid());
    }

    public static DatabaseReference to(Chat chat) {
        return append(CHATS, chat.getId());
    }

    public static DatabaseReference firstUserPreferences(Chat chat) {
        return append(to(chat), Constants.FIRST_USER, Constants.PREFERENCES);
    }

    public static DatabaseReference secondUserPreferences(Chat chat) {
        return append(to(chat), Constants.SECOND_USER, Constants.PREFERENCES);
    }

    static DatabaseReference append(String... strings) {
        return append(FirebaseDatabase.getInstance().getReference(), strings);
    }

    static DatabaseReference append(DatabaseReference ref, String... strings) {
        if (strings.length > 0) {
            ref = ref.child(strings[0]);
            strings = Arrays.copyOfRange(strings, 1, strings.length);
            return append(ref, strings);
        } else {
            return ref;
        }
    }

    public static String to(UserAttribute attribute) {
        switch(attribute) {
            case NAME:
                return Constants.DISPLAY_NAME;
            case ID:
                return Constants.USER_ID;
            case NOTIFICATIONS:
                return Constants.makePath(Constants.PREFERENCES, Constants.NOTIFICATIONS);
            case LANGUAGE:
                return Constants.makePath(Constants.PREFERENCES, Constants.LANGUAGE);
            case
            default:
                return Constants.UNDEFINED;
        }
    }
}
