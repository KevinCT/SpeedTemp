package com.zweigbergk.speedswede.util.collection;

import android.support.annotation.NonNull;

import com.zweigbergk.speedswede.util.Stringify;
import com.zweigbergk.speedswede.util.methodwrapper.Client;
import com.zweigbergk.speedswede.util.methodwrapper.Query;

import static com.zweigbergk.speedswede.util.collection.Collections.SizeMismatchException;

import java.util.Iterator;

@SuppressWarnings("Convert2streamapi")
public class HashMapExtension<K, V> extends java.util.HashMap<K, V> implements MapExtension<K, V> {

    @Override
    @NonNull
    public SetExtension<V> values() {
        SetExtension<V> result = new HashSetExtension<>();
        Client<MapExtension.Entry<K, V>> addValue = entry -> result.add(entry.getValue());
        foreach(addValue);

        return result;
    }


    @Override
    public void foreach(Client<Entry<K, V>> client) {
        for (MapExtension.Entry<K, V> item : this.entrySet()) {
            client.supply(item);
        }
    }

    @SuppressWarnings("unused")
    public MapExtension<K, V> filter(Query<Entry<K, V>> query) {
        MapExtension<K, V> result = new HashMapExtension<>();
        Client<MapExtension.Entry<K, V>> addMatches = entry -> {
          if (query.matches(entry)) {
              result.put(entry.getKey(), entry.getValue());
          }
        };

        foreach(addMatches);

        return result;
    }

    private MapExtension<K, V> reject(Query<Entry<K, V>> query) {
        MapExtension<K, V> result = new HashMapExtension<>();
        Client<MapExtension.Entry<K, V>> addMatches = entry -> {
            if (!query.matches(entry)) {
                result.put(entry.getKey(), entry.getValue());
            }
        };

        foreach(addMatches);

        return result;
    }

    @Override
    public MapExtension<K, V> nonNull() {
        Query<MapExtension.Entry<K, V>> isNull = entry -> entry.getValue() == null;

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

    public void putEntry(MapExtension.Entry<K, V> entry) {
        this.put(entry.getKey(), entry.getValue());
    }


}
