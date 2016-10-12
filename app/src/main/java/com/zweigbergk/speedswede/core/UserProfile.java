package com.zweigbergk.speedswede.core;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.Exclude;
import com.zweigbergk.speedswede.util.ParcelHelper;
import com.zweigbergk.speedswede.util.PreferenceValue;

import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class UserProfile implements User {

    private String mName, mUid;
//    private Timer timer;
//    private int[] matchingInterval;
//    private int ownRating;
    private MatchSkill matchSkill;
    private MatchSkill ownSkill;

    @Exclude
    private Map<Preference, PreferenceValue> mPreferences;

    public UserProfile(String name, String uid) {
        mName = name;
        mUid = uid;
//        timer = new Timer();
//        matchingInterval = new int[2];
//        ownRating = 0;
        matchSkill = MatchSkill.BEGINNER;
        ownSkill = MatchSkill.BEGINNER;
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
    @Exclude
    public Object getPreference(Preference preference) {
        return mPreferences.get(preference).getValue();
    }

    @Override
    @Exclude
    public Map<Preference, PreferenceValue> getPreferences() {
        return mPreferences;
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

    private UserProfile(Parcel in) {
        mName = in.readString();
        mUid = in.readString();
        mPreferences = ParcelHelper.readParcelableMap(in, Preference.class, PreferenceValue.class);
    }

    public MatchSkill getMatchSkill() {
        return matchSkill;
    }
    public MatchSkill getOwnSkill() {
        return ownSkill;
    }

    public void setOwnSkill(MatchSkill skill) {
        matchSkill = skill;
        switch(skill) {
            case BEGINNER:
                matchSkill = MatchSkill.SKILLED;
                break;
            case INTERMEDIATE:
                matchSkill = MatchSkill.INTERMEDIATE;
                break;
            case SKILLED:
                matchSkill = MatchSkill.BEGINNER;
                break;
            default:
                matchSkill = MatchSkill.BEGINNER;
        }
    }

//    public int getOwnRating() {
//        return this.ownRating;
//    }
//
//    public void setInitialMatchInterval() {
//        switch(matchSkill) {
//            case BEGINNER:
//                matchingInterval[0] = 0;
//                matchingInterval[1] = 0;
//                break;
//            case INTERMEDIATE:
//                matchingInterval[0] = 50;
//                matchingInterval[1] = 50;
//                break;
//            case SKILLED:
//                matchingInterval[0] = 100;
//                matchingInterval[0] = 100;
//                break;
//            default:
//                break;
//        }
//    }
//
//    public void setMatchingSkill(MatchSkill skill) {
//        matchSkill = skill;
//    }
//
//    public int[] getMatchInterval() {
//        return matchingInterval;
//    }
//
//    public void incrementRating() {
//        if(matchingInterval[0] >= 10) {
//            matchingInterval[0] = matchingInterval[0] - 10;
//        }
//        if(matchingInterval[1] <= 90) {
//            matchingInterval[1] = matchingInterval[1] + 10;
//        }
//    }
//
//    public void startTime() {
//        timer.scheduleAtFixedRate(new TimerTask() {
//            @Override
//            public void run() {
//                incrementRating();
//            }
//        }, 60*1000, 60*1000);
//    }
//
//    public void stopTime() {
//        timer.cancel();
//    }
}