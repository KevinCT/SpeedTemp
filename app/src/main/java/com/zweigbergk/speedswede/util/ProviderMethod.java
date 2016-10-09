package com.zweigbergk.speedswede.util;

public interface ProviderMethod<Item, Dependency> {
    Item call(Dependency dependency);
}
