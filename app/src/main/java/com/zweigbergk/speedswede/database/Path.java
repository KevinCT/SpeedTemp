package com.zweigbergk.speedswede.database;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.zweigbergk.speedswede.Constants;
import com.zweigbergk.speedswede.core.User;

import java.util.Arrays;

class Path {
    private static final DatabaseReference root = FirebaseDatabase.getInstance().getReference();

    private static final DatabaseReference USERS = root.child(Constants.USERS);

    public static DatabaseReference to(User user) {
        return combine(USERS, user.getUid());
    }

    @SuppressWarnings("WeakerAccess")
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
