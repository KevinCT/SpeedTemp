package com.zweigbergk.speedswede.util;

import com.zweigbergk.speedswede.util.collection.MapExtension;

public class Tuple<K, V> implements MapExtension.Entry<K, V> {

    private K key;
    private V value;

    public Tuple(K key, V value) {
        this.key = key;
        this.value = value;
    }

    @Override
    public K getKey() {
        return key;
    }

    @Override
    public V getValue() {
        return value;
    }

    @Override
    public V setValue(V value) {
        V oldValue = this.value;

        this.value = value;

        return oldValue;
    }

    @SuppressWarnings("unused")
    public static <K, V> Tuple from(MapExtension.Entry<K, V> entry) {
        return new Tuple<>(entry.getKey(), entry.getValue());
    }

    @Override
    public String toString() {
        return Stringify.curlyFormat("Tuple: key: {key}, value: {value}", key.toString(), value.toString());
    }

    @Override
    public int hashCode() {
        return this.key.hashCode() * 3 +
                this.value.hashCode() * 5;
    }
}
