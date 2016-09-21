package com.zweigbergk.speedswede.core;

import android.net.Uri;

import com.google.firebase.auth.FirebaseUser;

public class UserProfile implements User {

    private String mName, mUid;

    public UserProfile(String name, String uid) {
        mName = name;
        mUid = uid;
    }

    public static UserProfile from(FirebaseUser user) {
        return new UserProfile(user.getDisplayName(), user.getUid());
    }

    @Override
    public String getUid() {
        return mUid;
    }

    @Override
    public boolean isAnonymous() {
        return false;
    }

    @Override
    public String getDisplayName() {
        return mName;
    }

    @Override
    public Uri getPhotoUrl() {
        return null;
    }

    @Override
    public String getEmail() {
        return null;
    }
}