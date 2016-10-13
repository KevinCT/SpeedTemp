package com.zweigbergk.speedswede.core;

import android.os.Parcel;
import android.os.Parcelable;

import com.zweigbergk.speedswede.util.PreferenceValue;

import java.util.Arrays;
import java.util.Map;

public interface User extends Parcelable {
    String getUid();
    String getDisplayName();
    Object getPreference(Preference preference);
    Map<Preference, PreferenceValue> getPreferences();
    long getTimeInQueue();
    SkillCategory getSkillCategory();
    void startTime();
//    int getOwnRating();
//    int[] getMatchInterval();
//    void incrementRating();
//    void startTime();
//    void stopTime();
//    void setInitialMatchInterval();
//    void setMatchingSkill(MatchSkill skill);

    enum Preference implements Parcelable {
        NOTIFICATIONS, LANGUAGE, SKILL_CATEGORY;

        private final int mValue;

        public static Preference[] values = Preference.values();

        private static final Preference[] booleans = new Preference[] { NOTIFICATIONS };
        private static final Preference[] strings = new Preference[] { LANGUAGE, SKILL_CATEGORY};
        //private static final Preference[] longs = new Preference[] { };

        public boolean accepts(boolean value) {
            return Arrays.asList(booleans).contains(this);
        }

        public boolean accepts(String value) {
            return Arrays.asList(strings).contains(this);
        }

       /* public boolean accepts(long value) {
            return Arrays.asList(longs).contains(this);
        }*/

        Preference() {
            mValue = ordinal();
        }

        public static Preference fromInt(int i) {
            return Preference.values[i];
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(this.mValue);
        }

        @Override
        public int describeContents() {
            return 0;
        }

        public static final Creator<Preference> CREATOR = new Creator<Preference>() {
            @Override
            public Preference createFromParcel(Parcel in) {
                return Preference.fromInt(in.readInt());
            }

            @Override
            public Preference[] newArray(int size) {
                return new Preference[size];
            }
        };
    }
}
