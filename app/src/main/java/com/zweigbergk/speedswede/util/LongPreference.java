package com.zweigbergk.speedswede.util;

import android.os.Parcel;

public class LongPreference extends PreferenceValue<Long> {
    public LongPreference(Long value) {
        super(value);
    }

    public static final Creator<LongPreference> CREATOR = new Creator<LongPreference>() {
        public LongPreference createFromParcel(Parcel in) {
            return new LongPreference(in);
        }

        public LongPreference[] newArray(int size) {
            return new LongPreference[size];
        }
    };

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        super.writeToParcel(out, flags);
        out.writeLong(mValue);
    }

    private LongPreference(Parcel in) {
        super(in.readLong());
    }

    @Override
    public boolean equals(Object other) {
        if (other == null) {
            return false;
        }

        if (!other.getClass().equals(this.getClass())) {
            return false;
        }

        LongPreference otherLongPreference = (LongPreference) other;

        return otherLongPreference.getValue() == this.getValue();
    }
}
