package com.zweigbergk.speedswede.database;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.zweigbergk.speedswede.Constants;
import com.zweigbergk.speedswede.core.Chat;

import java.util.Arrays;

public class DatabasePath {
    private static final DatabaseReference root = FirebaseDatabase.getInstance().getReference();

    public static final DatabaseReference CHATS = root.child(Constants.CHATS);
    public static final DatabaseReference USERS = root.child(Constants.USERS);

    public static DatabaseReference toChat(String uid) {
        return databasePath(CHATS, uid);
    }

    public static DatabaseReference getPath(Chat chat) {
        return databasePath(CHATS, chat.getId());
    }

    public static DatabaseReference firstUserPreferences(Chat chat) {
        return databasePath(getPath(chat), Constants.FIRST_USER, Constants.PREFERENCES);
    }

    public static DatabaseReference secondUserPreferences(Chat chat) {
        return databasePath(getPath(chat), Constants.SECOND_USER, Constants.PREFERENCES);
    }

    static DatabaseReference databasePath(String... strings) {
        return databasePath(FirebaseDatabase.getInstance().getReference(), strings);
    }

    static DatabaseReference databasePath(DatabaseReference ref, String... strings) {
        if (strings.length > 0) {
            ref = ref.child(strings[0]);
            strings = Arrays.copyOfRange(strings, 1, strings.length);
            return databasePath(ref, strings);
        } else {
            return ref;
        }
    }
}
