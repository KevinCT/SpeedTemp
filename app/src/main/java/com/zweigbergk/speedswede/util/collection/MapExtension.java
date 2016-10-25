package com.zweigbergk.speedswede.util.collection;

import android.support.annotation.NonNull;

import com.zweigbergk.speedswede.util.Lists;
import com.zweigbergk.speedswede.util.methodwrapper.Client;
import com.zweigbergk.speedswede.util.methodwrapper.Query;

public interface MapExtension<K, V> extends java.util.Map<K, V> {
    <X, Y> MapExtension<X, Y> map(Lists.EntryMapping<X, Y> mapping);
    <E> ListExtension<E> transform(Lists.Mapping<MapExtension.Entry<K, V>, E> mapping);
    SetExtension<K> keys();

    void foreach(Client<Entry<K, V>> client);

    @NonNull
    SetExtension<V> values();

    MapExtension<V, K> invert();
    MapExtension<K, V> filter(Query<Entry<K, V>> query);
    MapExtension<K, V> reject(Query<Entry<K, V>> query);
    MapExtension<K, V> nonNull();
}