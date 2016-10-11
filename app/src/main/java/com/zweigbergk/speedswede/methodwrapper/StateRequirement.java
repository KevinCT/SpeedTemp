package com.zweigbergk.speedswede.methodwrapper;

public interface StateRequirement<T> {
    boolean isFulfilled(T object);
}
