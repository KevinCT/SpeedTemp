package com.zweigbergk.speedswede.util;

import android.os.Parcel;

public class LongPref extends PreferenceValue<Long> {
    public LongPref(Long value) {
        super(value);
    }

    public static final Creator<LongPref> CREATOR = new Creator<LongPref>() {
        public LongPref createFromParcel(Parcel in) {
            return new LongPref(in);
        }

        public LongPref[] newArray(int size) {
            return new LongPref[size];
        }
    };

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        super.writeToParcel(out, flags);
        out.writeLong(mValue);
    }

    private LongPref(Parcel in) {
        super(in.readLong());
    }
}
