package com.zweigbergk.speedswede.methodwrapper;

/**
 * Returns an item when provided with an appropriate dependency.
 * @param <Item> Type of item to return
 * @param <Dependency> Type of required object on which the method depends.
 */
public interface ProviderMethod<Item, Dependency> {
    Item call(Dependency dependency);
}
