package com.zweigbergk.speedswede.util.collection;

import android.support.annotation.NonNull;

import com.zweigbergk.speedswede.util.Lists;
import com.zweigbergk.speedswede.util.methodwrapper.Client;
import com.zweigbergk.speedswede.util.methodwrapper.Query;

public class HashMap<K, V> extends java.util.HashMap<K, V> implements Map<K, V> {
    @Override
    public <X, Y> Map<X, Y> map(Lists.EntryMapping<X, Y> mapping) {
        Map<X, Y> result = new HashMap<>();
        Client<Map.Entry<K, V>> addMapping = entry -> {
            Map.Entry<X, Y> mappedEntry = mapping.map(entry);
            result.put(mappedEntry.getKey(), mappedEntry.getValue());
        };
        forEach(addMapping);

        return result;
    }

    @Override
    public <E> List<E> transform(Lists.Mapping<Entry<K, V>, E> mapping) {
        List<E> result = new ArrayList<>();
        Client<Map.Entry<K, V>> addTransform = entry -> result.add(mapping.map(entry));
        forEach(addTransform);

        return result;
    }

    @Override
    @NonNull
    public Set<V> values() {
        Set<V> result = new HashSet<>();
        Client<Map.Entry<K, V>> addValue = entry -> result.add(entry.getValue());
        forEach(addValue);

        return result;
    }

    @Override
    public Set<K> keys() {
        Set<K> result = new HashSet<>();
        Client<Map.Entry<K, V>> addKey = entry -> result.add(entry.getKey());
        forEach(addKey);

        return result;
    }

    @Override
    public void forEach(Client<Entry<K, V>> client) {
        for (Map.Entry<K, V> item : this.entrySet()) {
            client.supply(item);
        }
    }

    @Override
    public Map<V, K> invert() {
        Map<V, K> result = new HashMap<>();
        Client<Map.Entry<K, V>> addInvertedEntry = entry -> result.put(entry.getValue(), entry.getKey());
        forEach(addInvertedEntry);

        return result;
    }

    @Override
    public Map<K, V> filter(Query<Entry<K, V>> query) {
        Map<K, V> result = new HashMap<>();
        Client<Map.Entry<K, V>> addMatches = entry -> {
          if (query.matches(entry)) {
              result.put(entry.getKey(), entry.getValue());
          }
        };

        forEach(addMatches);

        return result;
    }

    @Override
    public Map<K, V> reject(Query<Entry<K, V>> query) {
        Map<K, V> result = new HashMap<>();
        Client<Map.Entry<K, V>> addMatches = entry -> {
            if (!query.matches(entry)) {
                result.put(entry.getKey(), entry.getValue());
            }
        };

        forEach(addMatches);

        return result;
    }

    @Override
    public Map<K, V> nonNull() {
        Query<Map.Entry<K, V>> isNull = entry -> entry.getValue() != null;

        return reject(isNull);
    }
}
