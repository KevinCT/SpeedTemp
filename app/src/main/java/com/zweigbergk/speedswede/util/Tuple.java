package com.zweigbergk.speedswede.util;

import java.util.Map;

public class Tuple<K, V> implements Map.Entry<K, V> {

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

    public static <K, V> Tuple from(Map.Entry<K, V> entry) {
        return new Tuple<>(entry.getKey(), entry.getValue());
    }
}
