package com.zweigbergk.speedswede.util.collection;

import android.support.annotation.NonNull;

import com.zweigbergk.speedswede.util.methodwrapper.Client;

public interface MapExtension<K, V> extends java.util.Map<K, V> {

    void foreach(Client<Entry<K, V>> client);

    @NonNull
    SetExtension<V> values();

    MapExtension<K, V> nonNull();
    void putEntry(MapExtension.Entry<K, V> entry);
}