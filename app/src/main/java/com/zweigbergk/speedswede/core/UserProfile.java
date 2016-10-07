package com.zweigbergk.speedswede.core;

import com.google.firebase.auth.FirebaseUser;

import java.util.Map;

public class UserProfile implements User {

    private String mName, mUid;

    private Map<Preference, Object> mPreferences;

    public UserProfile(String name, String uid) {
        mName = name;
        mUid = uid;
    }

    public UserProfile withPreferences(Map<Preference, Object> preferences) {
        mPreferences = preferences;
        return this;
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
    public Object getPreference(Preference preference) {
        return mPreferences.get(preference);
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