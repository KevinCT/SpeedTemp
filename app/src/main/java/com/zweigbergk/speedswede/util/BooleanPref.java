package com.zweigbergk.speedswede.util;

import android.os.Parcel;
import android.os.Parcelable;

public class BooleanPref extends PreferenceValue<Boolean> {
    public BooleanPref(Boolean value) {
        super(value);
    }

    public static final Parcelable.Creator<BooleanPref> CREATOR = new Parcelable.Creator<BooleanPref>() {
        public BooleanPref createFromParcel(Parcel in) {
            return new BooleanPref(in);
        }

        public BooleanPref[] newArray(int size) {
            return new BooleanPref[size];
        }
    };

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        super.writeToParcel(out, flags);
        out.writeInt(getValue() ? 1 : 0);
    }

    private BooleanPref(Parcel in) {
        super(in.readInt() != 0);
    }
}
