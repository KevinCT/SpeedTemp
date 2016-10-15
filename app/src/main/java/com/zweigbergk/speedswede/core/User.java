package com.zweigbergk.speedswede.core;

import android.os.Parcel;
import android.os.Parcelable;

import com.zweigbergk.speedswede.Constants;
import com.zweigbergk.speedswede.activity.Language;
import com.zweigbergk.speedswede.database.UserReference;
import com.zweigbergk.speedswede.util.PreferenceValue;
import com.zweigbergk.speedswede.util.collection.Map;

import java.util.Arrays;

public interface User extends Parcelable {
    String getUid();
    String getDisplayName();

    //Preferences
    PreferenceValue getPreference(Preference preference);
    Map<Preference, PreferenceValue> getPreferences();
    boolean getNotificationPreference();
    Language getLanguage();
    SkillCategory getSkillCategory();

    long getTimeInQueue();

    boolean isFirstLogin();
    void setFirstLogin(boolean value);
    void startTime();
//    int getOwnRating();
//    int[] getMatchInterval();
//    void incrementRating();
//    void startTime();
//    void stopTime();
//    void setInitialMatchInterval();
//    void setMatchingSkill(MatchSkill skill);

    enum Preference implements Parcelable {
        NOTIFICATIONS(Constants.NOTIFICATIONS), LANGUAGE(Constants.LANGUAGE), SKILL_CATEGORY(Constants.SKILL_CATEGORY);

        private final String value;

        public static Preference[] values = Preference.values();

        private static final Preference[] booleans = new Preference[] { NOTIFICATIONS };
        private static final Preference[] strings = new Preference[] { LANGUAGE, SKILL_CATEGORY};
        //private static final Preference[] longs = new Preference[] { };

        public boolean accepts(boolean value) {
            return Arrays.asList(booleans).contains(value);
        }

       /* public boolean accepts(long value) {
            return Arrays.asList(longs).contains(this);
        }*/

        Preference(String value) {
            this.value = value;
        }

        public static Preference fromString(String text) {
            if (text != null) {
                for (Preference preference : Preference.values()) {
                    if (text.equalsIgnoreCase(preference.value)) {
                        return preference;
                    }
                }
            }
            return null;
        }


        public static Preference fromInt(int i) {
            return Preference.values[i];
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.value);
        }

        @Override
        public int describeContents() {
            return 0;
        }

        public static final Creator<Preference> CREATOR = new Creator<Preference>() {
            @Override
            public Preference createFromParcel(Parcel in) {
                return Preference.fromString(in.readString());
            }

            @Override
            public Preference[] newArray(int size) {
                return new Preference[size];
            }
        };
    }
}
