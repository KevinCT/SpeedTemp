package com.zweigbergk.speedswede.core;

import com.google.firebase.auth.FirebaseUser;

public class UserProfile implements User {

    private String mName, mUid;

    public UserProfile(String name, String uid) {
        mName = name;
        mUid = uid;
    }

    @Override
    public String getUid() {
        return mUid;
    }

    @Override
    public String getDisplayName() {
        return mName;
    }

    public static UserProfile from(FirebaseUser user) {
        return user == null ? null : new UserProfile(user.getDisplayName(), user.getUid());
    }

    @Override
    public int hashCode() {
        return mUid.hashCode();
    }

    @Override
    public String toString() {
        return this.getUid();
    }

    @Override
    public boolean equals(Object other) {
        if (other == null) {
            return false;
        }

        if (!other.getClass().equals(this.getClass())) {
            return false;
        }

        UserProfile otherUserProfile = (UserProfile) other;

        return otherUserProfile.getUid().equals(this.getUid());
    }
}