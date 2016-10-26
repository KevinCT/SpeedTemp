package com.zweigbergk.speedswede.mock;

public interface ISnapshot {
    ISnapshot child(String path);
    Object getValue();
    String getKey();
    void setValue(Object value);
    Iterable<ISnapshot> getChildren();
    int getChildrenCount();
    boolean exists();
}
