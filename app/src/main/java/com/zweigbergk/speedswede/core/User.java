package com.zweigbergk.speedswede.core;

import android.os.Parcel;
import android.os.Parcelable;

import com.zweigbergk.speedswede.Constants;
import com.zweigbergk.speedswede.util.Language;
import com.zweigbergk.speedswede.util.PreferenceWrapper;
import com.zweigbergk.speedswede.util.collection.MapExtension;

public interface User extends Parcelable {
    String getUid();
    String getDisplayName();

    //Preferences
    @SuppressWarnings("unused")
    PreferenceWrapper getPreference(Preference preference);
    MapExtension<Preference, PreferenceWrapper> getPreferences();
    @SuppressWarnings("unused")
    Language getLanguage();
    SkillCategory getSkillCategory();
    void setPreference(Preference preference, PreferenceWrapper wrapper);

    long getTimeInQueue();

    boolean isFirstLogin();
    void setFirstLogin(boolean value);

    enum Preference implements Parcelable {
        NOTIFICATIONS(Constants.NOTIFICATIONS), LANGUAGE(Constants.LANGUAGE), SKILL_CATEGORY(Constants.SKILL_CATEGORY);

        private final String value;

        public String toString() {
            return value;
        }

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
