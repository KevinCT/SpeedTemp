package com.zweigbergk.speedswede.service;

public class DataChange<ObjectType> {

    private final ConversationEvent mEvent;
    private final ObjectType mData;

    DataChange(ObjectType data, ConversationEvent event) {
        mData = data;
        mEvent = event;
    }

    public ObjectType getItem() {
        return mData;
    }

    public ConversationEvent getEvent() {
        return mEvent;
    }

    public static <ObjectType> DataChange<ObjectType> added(ObjectType data) {
        return new DataChange<>(data, ConversationEvent.MESSAGE_ADDED);
    }

    public static <ObjectType> DataChange<ObjectType> modified(ObjectType data) {
        return new DataChange<>(data, ConversationEvent.MESSAGE_MODIFIED);
    }

    public static <ObjectType> DataChange<ObjectType> removed(ObjectType data) {
        return new DataChange<>(data, ConversationEvent.MESSAGE_REMOVED);
    }

    public static <ObjectType> DataChange<ObjectType> cancelled(ObjectType data) {
        return new DataChange<>(data, ConversationEvent.INTERRUPED);
    }
}