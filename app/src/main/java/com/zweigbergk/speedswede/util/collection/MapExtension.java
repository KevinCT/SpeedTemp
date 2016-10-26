package com.zweigbergk.speedswede.util.collection;

import android.support.annotation.NonNull;

import static com.zweigbergk.speedswede.util.collection.CollectionExtension.EntryMapping;
import static com.zweigbergk.speedswede.util.collection.CollectionExtension.Mapping;
import com.zweigbergk.speedswede.util.methodwrapper.Client;
import com.zweigbergk.speedswede.util.methodwrapper.Query;

import java.util.Map;

public interface MapExtension<K, V> extends java.util.Map<K, V> {
    <X, Y> MapExtension<X, Y> map(EntryMapping<K, V> mapping);
    <E> ListExtension<E> transform(Mapping<Entry<K, V>, E> mapping);
    <E> SetExtension<E> transformToSet(Mapping<Entry<K, V>, E> mapping);
    SetExtension<K> keys();

    void foreach(Client<Entry<K, V>> client);

    @NonNull
    SetExtension<V> values();

    MapExtension<V, K> invert();
    MapExtension<K, V> filter(Query<Entry<K, V>> query);
    MapExtension<K, V> reject(Query<Entry<K, V>> query);
    MapExtension<K, V> nonNull();
    void putEntry(Entry<K, V> entry);

    class MapEntry<K, V> implements MapExtension.Entry<K, V> {

        private final K key;
        private V value;

        public MapEntry(K key, V value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public K getKey() {
            return this.key;
        }

        @Override
        public V getValue() {
            return this.value;
        }

        @Override
        public V setValue(V value) {
            V old = this.value;
            this.value = value;
            return old;
        }
    }
}