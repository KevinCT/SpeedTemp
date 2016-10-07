package com.zweigbergk.speedswede.database;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.zweigbergk.speedswede.Constants;
import com.zweigbergk.speedswede.core.User;

public class PoolUserCheck extends UserCheck {

    public static UserCheck ifExists(User user) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference()
                .child(Constants.POOL).child(user.getUid());
        return UserCheck.ifExists(user, ref);
    }
}
