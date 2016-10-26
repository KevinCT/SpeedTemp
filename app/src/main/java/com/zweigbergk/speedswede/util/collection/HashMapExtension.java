package com.zweigbergk.speedswede.util.collection;

import android.support.annotation.NonNull;

import com.zweigbergk.speedswede.util.Stringify;
import com.zweigbergk.speedswede.util.methodwrapper.Client;
import com.zweigbergk.speedswede.util.methodwrapper.Query;

import static com.zweigbergk.speedswede.util.collection.CollectionExtension.Mapping;
import static com.zweigbergk.speedswede.util.collection.Collections.SizeMismatchException;

import java.util.Iterator;

@SuppressWarnings("Convert2streamapi")
public class HashMapExtension<K, V> extends java.util.HashMap<K, V> implements MapExtension<K, V> {
    @Override
    public <X, Y> MapExtension<X, Y> map(CollectionExtension.EntryMapping<K, V> mapping) {
        MapExtension<X, Y> result = new HashMapExtension<>();
        Client<MapExtension.Entry<K, V>> addMapping = entry -> {
            MapExtension.Entry<X, Y> mappedEntry = mapping.map(entry);
            result.put(mappedEntry.getKey(), mappedEntry.getValue());
        };
        foreach(addMapping);

        return result;
    }

    @Override
    public <E> ListExtension<E> transform(Mapping<Entry<K, V>, E> mapping) {
        ListExtension<E> result = new ArrayListExtension<>();
        Client<Entry<K, V>> addTransform = entry -> result.add(mapping.map(entry));
        foreach(addTransform);

        return result;
    }

    @Override
    public <E> SetExtension<E> transformToSet(Mapping<Entry<K, V>, E> mapping) {
        SetExtension<E> result = new HashSetExtension<>();
        Client<Entry<K, V>> addTransform = entry -> result.add(mapping.map(entry));
        foreach(addTransform);

        return result;
    }

    @Override
    @NonNull
    public SetExtension<V> values() {
        SetExtension<V> result = new HashSetExtension<>();
        Client<Entry<K, V>> addValue = entry -> result.add(entry.getValue());
        foreach(addValue);

        return result;
    }

    @Override
    public SetExtension<K> keys() {
        SetExtension<K> result = new HashSetExtension<>();
        Client<Entry<K, V>> addKey = entry -> result.add(entry.getKey());
        foreach(addKey);

        return result;
    }

    @Override
    public void foreach(Client<Entry<K, V>> client) {
        for (Entry<K, V> item : this.entrySet()) {
            client.supply(item);
        }
    }

    @Override
    public MapExtension<V, K> invert() {
        MapExtension<V, K> result = new HashMapExtension<>();
        Client<Entry<K, V>> addInvertedEntry = entry -> result.put(entry.getValue(), entry.getKey());
        foreach(addInvertedEntry);

        return result;
    }

    @Override
    public MapExtension<K, V> filter(Query<Entry<K, V>> query) {
        MapExtension<K, V> result = new HashMapExtension<>();
        Client<Entry<K, V>> addMatches = entry -> {
            if (query.matches(entry)) {
                result.put(entry.getKey(), entry.getValue());
            }
        };

        foreach(addMatches);

        return result;
    }

    @Override
    public MapExtension<K, V> reject(Query<Entry<K, V>> query) {
        MapExtension<K, V> result = new HashMapExtension<>();
        Client<Entry<K, V>> addMatches = entry -> {
            if (!query.matches(entry)) {
                result.put(entry.getKey(), entry.getValue());
            }
        };

        foreach(addMatches);

        return result;
    }

    @Override
    public MapExtension<K, V> nonNull() {
        Query<Entry<K, V>> isNull = entry -> entry.getValue() == null;

        return reject(isNull);
    }

    public static <K, V> MapExtension<K, V> create(CollectionExtension<K> keys, CollectionExtension<V> values) {
        if (keys.size() != values.size()) {
            throw new SizeMismatchException(Stringify.curlyFormat(
                    "Key collection (size: {keyCount}) must be of same size as value collection" +
                            " (size: {valueCount}).", keys.size(), values.size())
            );
        }

        MapExtension<K, V> result = new HashMapExtension<>();
        Iterator<K> keyIterator = keys.iterator();
        Iterator<V> valueIterator = values.iterator();

        while(keyIterator.hasNext()) {
            result.put(keyIterator.next(), valueIterator.next());
        }

        return result;
    }

    public void putEntry(Entry<K, V> entry) {
        put(entry.getKey(), entry.getValue());
    }

}
