package com.zweigbergk.speedswede.util.collection;

import android.support.annotation.NonNull;

import com.zweigbergk.speedswede.util.Lists;
import com.zweigbergk.speedswede.util.methodwrapper.Client;
import com.zweigbergk.speedswede.util.methodwrapper.Query;

public interface Map<K, V> extends java.util.Map<K, V> {
    <X, Y> Map<X, Y> map(Lists.EntryMapping<X, Y> mapping);
    <E> List<E> transform(Lists.Mapping<Map.Entry<K, V>, E> mapping);
    Set<K> keys();

    void foreach(Client<Entry<K, V>> client);

    @NonNull
    Set<V> values();

    Map<V, K> invert();
    Map<K, V> filter(Query<Entry<K, V>> query);
    Map<K, V> reject(Query<Entry<K, V>> query);
    Map<K, V> nonNull();
}