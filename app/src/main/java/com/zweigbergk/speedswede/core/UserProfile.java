package com.zweigbergk.speedswede.core;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.auth.FirebaseUser;
import com.zweigbergk.speedswede.util.ParcelHelper;
import com.zweigbergk.speedswede.util.PreferenceValue;

import java.util.Map;

public class UserProfile implements User {

    private String mName, mUid;

    private Map<Preference, PreferenceValue> mPreferences;

    public UserProfile(String name, String uid) {
        mName = name;
        mUid = uid;
    }

    public UserProfile withPreferences(Map<Preference, PreferenceValue> preferences) {
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
        return mPreferences.get(preference).getValue();
    }

    @Override
    public Map<Preference, PreferenceValue> getPreferences() {
        return mPreferences;
    }

    @Override
    public Parcelable.Creator<User> getCreator() {
        return null;
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
        return String.format("UserProfile {\n\t\tname: %s,\n\t\tuid: %s,\n\t\tpreferences: %s\n}",
                mName, mUid, mPreferences);
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

    public static final Parcelable.Creator CREATOR
            = new Parcelable.Creator() {
        public UserProfile createFromParcel(Parcel in) {
            return new UserProfile(in);
        }

        public UserProfile[] newArray(int size) {
            return new UserProfile[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mName);
        dest.writeString(mUid);
        ParcelHelper.writeParcelableMap(dest, 0, mPreferences);
    }

    public UserProfile(Parcel in) {
        mName = in.readString();
        mUid = in.readString();
        mPreferences = ParcelHelper.readParcelableMap(in, Preference.class, PreferenceValue.class);
    }
}