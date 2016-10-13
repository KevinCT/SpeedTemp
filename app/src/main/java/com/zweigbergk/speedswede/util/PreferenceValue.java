package com.zweigbergk.speedswede.util;

import android.os.Parcel;
import android.os.Parcelable;

public abstract class PreferenceValue<T> implements Parcelable {

    T mValue;

    PreferenceValue(T value) {
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

    public static class StringValue extends PreferenceValue<String> {
        public StringValue(String value) {
            super(value);
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
            out.writeString(mValue);
        }

        private StringValue(Parcel in) {
            super(in.readString());
        }
    }

    public static class BooleanValue extends PreferenceValue<Boolean> {
        public BooleanValue(Boolean value) {
            super(value);
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
            out.writeLong(mValue);
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
