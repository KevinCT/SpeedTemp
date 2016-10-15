package com.zweigbergk.speedswede.util;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.zweigbergk.speedswede.Constants;

public abstract class PreferenceWrapper<T> implements Parcelable {
    private static final String TAG = PreferenceWrapper.class.getSimpleName().toUpperCase();

    private T mValue;

    private PreferenceWrapper(T value) {
        mValue = value;
    }

    public T getValue() {
        return mValue;
    }

    abstract PreferenceWrapper<T> withValue(T value);

    public static <T> PreferenceWrapper<T> cast(Object object) {
        for (PreferenceWrapper shell : Constants.shells) {
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

    public static class StringWrapper extends PreferenceWrapper<String> {
        public StringWrapper(String value) {
            super(value);
        }

        @Override
        PreferenceWrapper<String> withValue(String value) {
            return new StringWrapper(value);
        }

        public static StringWrapper shell() {
            return new StringWrapper("");
        }

        public static final Creator<StringWrapper> CREATOR = new Creator<StringWrapper>() {
            public StringWrapper createFromParcel(Parcel in) {
                return new StringWrapper(in);
            }

            public StringWrapper[] newArray(int size) {
                return new StringWrapper[size];
            }
        };

        public int describeContents() {
            return 0;
        }

        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeString(getValue());
        }

        private StringWrapper(Parcel in) {
            super(in.readString());
        }
    }

    public static class BooleanWrapper extends PreferenceWrapper<Boolean> {
        public BooleanWrapper(Boolean value) {
            super(value);
        }

        public static BooleanWrapper shell() {
            return new BooleanWrapper(true);
        }

        public BooleanWrapper withValue(Boolean value) {
            return new BooleanWrapper(value);
        }

        public static final Parcelable.Creator<BooleanWrapper> CREATOR = new Parcelable.Creator<BooleanWrapper>() {
            public BooleanWrapper createFromParcel(Parcel in) {
                return new BooleanWrapper(in);
            }

            public BooleanWrapper[] newArray(int size) {
                return new BooleanWrapper[size];
            }
        };

        public int describeContents() {
            return 0;
        }

        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeInt(getValue() ? 1 : 0);
        }

        private BooleanWrapper(Parcel in) {
            super(in.readInt() != 0);
        }
    }

    public static class LongWrapper extends PreferenceWrapper<Long> {
        public LongWrapper(Long value) {
            super(value);
        }

        public static LongWrapper shell() {
            return new LongWrapper(0L);
        }

        public LongWrapper withValue(Long value) {
            return new LongWrapper(value);
        }

        public static final Creator<LongWrapper> CREATOR = new Creator<LongWrapper>() {
            public LongWrapper createFromParcel(Parcel in) {
                return new LongWrapper(in);
            }

            public LongWrapper[] newArray(int size) {
                return new LongWrapper[size];
            }
        };

        public int describeContents() {
            return 0;
        }

        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeLong(getValue());
        }

        private LongWrapper(Parcel in) {
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

            LongWrapper other = (LongWrapper) object;

            return other.getValue().longValue() == this.getValue().longValue();
        }
    }
}
