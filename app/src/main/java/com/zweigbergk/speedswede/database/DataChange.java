package com.zweigbergk.speedswede.database;

public class DataChange<ObjectType> {

    private final DatabaseEvent mEvent;
    private final ObjectType mData;

    DataChange(ObjectType data, DatabaseEvent event) {
        mData = data;
        mEvent = event;
    }

    public ObjectType getItem() {
        return mData;
    }

    public DatabaseEvent getEvent() {
        return mEvent;
    }

    public static <ObjectType> DataChange<ObjectType> added(ObjectType data) {
        return new DataChange<>(data, DatabaseEvent.ADDED);
    }

    public static <ObjectType> DataChange<ObjectType> modified(ObjectType data) {
        return new DataChange<>(data, DatabaseEvent.CHANGED);
    }

    public static <ObjectType> DataChange<ObjectType> removed(ObjectType data) {
        return new DataChange<>(data, DatabaseEvent.REMOVED);
    }

    public static <ObjectType> DataChange<ObjectType> cancelled(ObjectType data) {
        return new DataChange<>(data, DatabaseEvent.INTERRUPTED);
    }
}