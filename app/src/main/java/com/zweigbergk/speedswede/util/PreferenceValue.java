package com.zweigbergk.speedswede.util;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.zweigbergk.speedswede.Constants;

public abstract class PreferenceValue<T> implements Parcelable {
    private static final String TAG = PreferenceValue.class.getSimpleName().toUpperCase();

    private T mValue;

    private PreferenceValue(T value) {
        mValue = value;
    }

    public T getValue() {
        return mValue;
    }

    abstract PreferenceValue<T> withValue(T value);

    public static <T> PreferenceValue<T> cast(Object object) {
        for (PreferenceValue shell : Constants.shells) {
            try {
                T item = (T) shell.getValue().getClass().cast(object);
                Log.d(TAG, "Returning from cast with: " + item.toString());
                return shell.withValue(item);
            } catch (ClassCastException e) {
                Log.d(TAG, "Could not cast to " + shell.getValue().getClass());
            }
        }

        return null;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public String toString() {
        return mValue.getClass().getSimpleName();
    }

    public static class StringValue extends PreferenceValue<String> {
        public StringValue(String value) {
            super(value);
        }

        @Override
        PreferenceValue<String> withValue(String value) {
            return new StringValue(value);
        }

        public static StringValue shell() {
            return new StringValue("");
        }

        public static final Creator<StringValue> CREATOR = new Creator<StringValue>() {
            public StringValue createFromParcel(Parcel in) {
                return new StringValue(in);
            }

            public StringValue[] newArray(int size) {
                return new StringValue[size];
            }
        };

        public int describeContents() {
            return 0;
        }

        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeString(getValue());
        }

        private StringValue(Parcel in) {
            super(in.readString());
        }
    }

    public static class BooleanValue extends PreferenceValue<Boolean> {
        public BooleanValue(Boolean value) {
            super(value);
        }

        public static BooleanValue shell() {
            return new BooleanValue(true);
        }

        public BooleanValue withValue(Boolean value) {
            return new BooleanValue(value);
        }

        public static final Parcelable.Creator<BooleanValue> CREATOR = new Parcelable.Creator<BooleanValue>() {
            public BooleanValue createFromParcel(Parcel in) {
                return new BooleanValue(in);
            }

            public BooleanValue[] newArray(int size) {
                return new BooleanValue[size];
            }
        };

        public int describeContents() {
            return 0;
        }

        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeInt(getValue() ? 1 : 0);
        }

        private BooleanValue(Parcel in) {
            super(in.readInt() != 0);
        }
    }

    public static class LongValue extends PreferenceValue<Long> {
        public LongValue(Long value) {
            super(value);
        }

        public static LongValue shell() {
            return new LongValue(0L);
        }

        public LongValue withValue(Long value) {
            return new LongValue(value);
        }

        public static final Creator<LongValue> CREATOR = new Creator<LongValue>() {
            public LongValue createFromParcel(Parcel in) {
                return new LongValue(in);
            }

            public LongValue[] newArray(int size) {
                return new LongValue[size];
            }
        };

        public int describeContents() {
            return 0;
        }

        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeLong(getValue());
        }

        private LongValue(Parcel in) {
            super(in.readLong());
        }

        @Override
        public boolean equals(Object object) {
            if (object == null) {
                return false;
            }

            if (!object.getClass().equals(this.getClass())) {
                return false;
            }

            LongValue other = (LongValue) object;

            return other.getValue().longValue() == this.getValue().longValue();
        }
    }
}
