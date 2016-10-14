package com.zweigbergk.speedswede.core;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.Exclude;
import com.zweigbergk.speedswede.util.ParcelHelper;
import com.zweigbergk.speedswede.util.PreferenceValue;
import com.zweigbergk.speedswede.util.collection.HashMap;
import com.zweigbergk.speedswede.util.collection.Map;

import java.util.Date;

public class UserProfile implements User {

    private String displayName, uid;
//    private Timer timer;
//    private int[] matchingInterval;

    private long timeInQueue;

    @Exclude
    private Map<Preference, PreferenceValue> mPreferences;

    public UserProfile(String name, String uid) {
        this.displayName = name;
        this.uid = uid;

        mPreferences = new HashMap<>();
    }

    public UserProfile withPreferences(Map<Preference, PreferenceValue> preferences) {
        setPreferences(preferences);
        return this;
    }

    @Exclude
    @Override
    public SkillCategory getSkillCategory() {
        return SkillCategory.fromString((String) getPreference(Preference.SKILL_CATEGORY).getValue());
}

    @Override
    public String getUid() {
        return uid;
    }

    @Override
    public String getDisplayName() {
        return displayName;
    }

    @Override
    @Exclude
    public PreferenceValue getPreference(Preference preference) {
        return mPreferences.get(preference);
    }

    private void setPreferences(java.util.Map<Preference, PreferenceValue> map) {
        for (Map.Entry<Preference, PreferenceValue> entry : map.entrySet()) {
            if (entry.getValue() != null) {
                mPreferences.put(entry.getKey(), entry.getValue());
            }
        }
    }

    @Override
    @Exclude
    public Map<Preference, PreferenceValue> getPreferences() {
        return mPreferences;
    }

    public static UserProfile from(FirebaseUser user) {
        if (user != null) {
            return new UserProfile(user.getDisplayName(), user.getUid());
        }
        return null;
    }

    @Override
    public int hashCode() {
        return uid.hashCode();
    }

    @Override
    public String toString() {
        return String.format("UserProfile {\n\t\tdisplayName: %s,\n\t\tuid: %s\n}",
                displayName, uid);
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
        dest.writeString(displayName);
        dest.writeString(uid);
        ParcelHelper.writeParcelableMap(dest, 0, mPreferences);
    }

    private UserProfile(Parcel in) {
        if (in.readString() != null) {
            displayName = in.readString();
            uid = in.readString();
            Map<Preference, PreferenceValue> preferences =
                    ParcelHelper.readParcelableMap(in, Preference.class, PreferenceValue.class);
            setPreferences(preferences);
        }
    }

    public void startTime() {
        Date date = new Date();
        timeInQueue = date.getTime();
    }

    public long getTimeInQueue() {
        return timeInQueue;
    }

//    public int getOwnRating() {
//        return this.ownRating;
//    }
//
//    public void setInitialMatchInterval() {
//        switch(matchSkill) {
//            case STUDENT:
//                matchingInterval[0] = 0;
//                matchingInterval[1] = 0;
//                break;
//            case CHATTER:
//                matchingInterval[0] = 50;
//                matchingInterval[1] = 50;
//                break;
//            case MENTOR:
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