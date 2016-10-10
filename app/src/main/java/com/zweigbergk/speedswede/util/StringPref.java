package com.zweigbergk.speedswede.util;

import android.os.Parcel;

public class StringPref extends PreferenceValue<String> {
    public StringPref(String value) {
        super(value);
    }

    public static final Creator<StringPref> CREATOR = new Creator<StringPref>() {
        public StringPref createFromParcel(Parcel in) {
            return new StringPref(in);
        }

        public StringPref[] newArray(int size) {
            return new StringPref[size];
        }
    };

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        super.writeToParcel(out, flags);
        out.writeString(mValue);
    }

    private StringPref(Parcel in) {
        super(in.readString());
    }
}
