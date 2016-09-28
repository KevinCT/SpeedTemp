package com.zweigbergk.speedswede.core;

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

    @Override
    public int hashCode() {
        return mUid.hashCode();
    }

    @Override
    public String toString() {
        return this.getUid();
    }
}