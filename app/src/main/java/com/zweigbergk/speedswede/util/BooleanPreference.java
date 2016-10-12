package com.zweigbergk.speedswede.util;

import android.os.Parcel;
import android.os.Parcelable;

public class BooleanPreference extends PreferenceValue<Boolean> {
    public BooleanPreference(Boolean value) {
        super(value);
    }

    public static final Parcelable.Creator<BooleanPreference> CREATOR = new Parcelable.Creator<BooleanPreference>() {
        public BooleanPreference createFromParcel(Parcel in) {
            return new BooleanPreference(in);
        }

        public BooleanPreference[] newArray(int size) {
            return new BooleanPreference[size];
        }
    };

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        super.writeToParcel(out, flags);
        out.writeInt(getValue() ? 1 : 0);
    }

    private BooleanPreference(Parcel in) {
        super(in.readInt() != 0);
    }
}
