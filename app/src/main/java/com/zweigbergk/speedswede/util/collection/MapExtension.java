package com.zweigbergk.speedswede.util.collection;

import android.support.annotation.NonNull;

import static com.zweigbergk.speedswede.util.collection.CollectionExtension.EntryMapping;
import static com.zweigbergk.speedswede.util.collection.CollectionExtension.Mapping;
import com.zweigbergk.speedswede.util.methodwrapper.Client;
import com.zweigbergk.speedswede.util.methodwrapper.Query;

public interface MapExtension<K, V> extends java.util.Map<K, V> {
    <X, Y> MapExtension<X, Y> map(EntryMapping<K, V> mapping);
    <E> ListExtension<E> transform(Mapping<MapExtension.Entry<K, V>, E> mapping);
    <E> SetExtension<E> transformToSet(Mapping<MapExtension.Entry<K, V>, E> mapping);
    SetExtension<K> keys();

    void foreach(Client<Entry<K, V>> client);

    @NonNull
    SetExtension<V> values();

    MapExtension<V, K> invert();
    MapExtension<K, V> filter(Query<Entry<K, V>> query);
    MapExtension<K, V> reject(Query<Entry<K, V>> query);
    MapExtension<K, V> nonNull();
    void putEntry(MapExtension.Entry<K, V> entry);
}