package com.zweigbergk.speedswede.util;

public interface StateRequirement<T> {
    boolean isFulfilled(T object);
}
