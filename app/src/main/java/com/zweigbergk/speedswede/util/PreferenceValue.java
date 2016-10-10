package com.zweigbergk.speedswede.util;

import android.os.Parcel;
import android.os.Parcelable;

public abstract class PreferenceValue<T> implements Parcelable {

    T mValue;

    public PreferenceValue(T value) {
        mValue = value;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

    }

    @Override
    public int describeContents() {
        return 0;
    }

    public T getValue() {
        return mValue;
    }

    public String toString() {
        return mValue.getClass().getSimpleName();
    }
}
