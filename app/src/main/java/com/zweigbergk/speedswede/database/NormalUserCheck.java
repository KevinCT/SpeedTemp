package com.zweigbergk.speedswede.database;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.zweigbergk.speedswede.Constants;
import com.zweigbergk.speedswede.core.User;

public class NormalUserCheck extends UserCheck {

    public static UserCheck ifExists(String userId) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference()
                .child(Constants.USERS).child(userId);
        return UserCheck.ifExists(userId, ref);
    }

    public static UserCheck ifExists(User user) {
        return ifExists(user.getUid());
    }
}
