package com.zweigbergk.speedswede.util.methodwrapper;

public interface StateRequirement<T> {
    boolean isFulfilled(T object);
}
