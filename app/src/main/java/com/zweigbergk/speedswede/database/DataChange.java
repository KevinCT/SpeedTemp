package com.zweigbergk.speedswede.database;

public class DataChange<T> {

    private final DatabaseEvent mEvent;
    private final T mData;

    DataChange(T data, DatabaseEvent event) {
        mData = data;
        mEvent = event;
    }

    public T getItem() {
        return mData;
    }

    public DatabaseEvent getEvent() {
        return mEvent;
    }

    public static <T> DataChange<T> added(T data) {
        return new DataChange<>(data, DatabaseEvent.ADDED);
    }

    public static <T> DataChange<T> modified(T data) {
        return new DataChange<>(data, DatabaseEvent.CHANGED);
    }

    public static <T> DataChange<T> removed(T data) {
        return new DataChange<>(data, DatabaseEvent.REMOVED);
    }

    public static <T> DataChange<T> cancelled(T data) {
        return new DataChange<>(data, DatabaseEvent.INTERRUPTED);
    }

    @Override
    public int hashCode() {
        return mEvent.hashCode() * 3 +
                mData.hashCode() * 5;
    }

    @Override
    public boolean equals(Object other) {
        if (other == null) {
            return false;
        }

        if (!other.getClass().equals(this.getClass())) {
            return false;
        }
        
        DataChange otherDataChange = (DataChange) other;
        return otherDataChange.getItem().equals(this.getItem()) &&
                otherDataChange.getEvent().equals(this.getEvent());
    }


}