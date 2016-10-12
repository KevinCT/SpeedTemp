package com.zweigbergk.speedswede.util.methodwrapper;

/**
 * Executes a method when provided with an appropriate dependency.
 * @param <Dependency> Type of required object on which the method depends.
 */
public interface CallerMethod<Dependency> {
    void call(Dependency dependency);
}
