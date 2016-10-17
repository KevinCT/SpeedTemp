package com.zweigbergk.speedswede.database;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.zweigbergk.speedswede.Constants;
import com.zweigbergk.speedswede.core.Chat;
import com.zweigbergk.speedswede.core.User;

import java.util.Arrays;

public class Path {
    private static final DatabaseReference root = FirebaseDatabase.getInstance().getReference();

    public static final DatabaseReference CHATS = root.child(Constants.CHATS);
    public static final DatabaseReference USERS = root.child(Constants.USERS);

    public static DatabaseReference toChat(String uid) {
        return combine(CHATS, uid);
    }

    public static DatabaseReference to(User user) {
        return combine(USERS, user.getUid());
    }

    public static DatabaseReference to(Chat chat) {
        return combine(CHATS, chat.getId());
    }

    public static DatabaseReference firstUserPreferences(Chat chat) {
        return combine(to(chat), Constants.FIRST_USER, Constants.PREFERENCES);
    }

    public static DatabaseReference secondUserPreferences(Chat chat) {
        return combine(to(chat), Constants.SECOND_USER, Constants.PREFERENCES);
    }

    static DatabaseReference combine(String... strings) {
        return combine(FirebaseDatabase.getInstance().getReference(), strings);
    }

    static DatabaseReference combine(DatabaseReference ref, String... strings) {
        if (strings.length > 0) {
            ref = ref.child(strings[0]);
            strings = Arrays.copyOfRange(strings, 1, strings.length);
            return combine(ref, strings);
        } else {
            return ref;
        }
    }
}
