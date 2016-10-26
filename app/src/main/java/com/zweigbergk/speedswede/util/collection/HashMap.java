package com.zweigbergk.speedswede.util.collection;

import android.support.annotation.NonNull;

import com.zweigbergk.speedswede.util.Lists;
import com.zweigbergk.speedswede.util.Stringify;
import com.zweigbergk.speedswede.util.methodwrapper.Client;
import com.zweigbergk.speedswede.util.methodwrapper.Query;

import static com.zweigbergk.speedswede.util.collection.Collections.SizeMismatchException;

import java.util.Iterator;

public class HashMap<K, V> extends java.util.HashMap<K, V> implements Map<K, V> {

    @Override
    public <X, Y> Map<X, Y> map(Lists.EntryMapping<X, Y> mapping) {
        Map<X, Y> result = new HashMap<>();
        Client<Map.Entry<K, V>> addMapping = entry -> {
            Map.Entry<X, Y> mappedEntry = mapping.map(entry);
            result.put(mappedEntry.getKey(), mappedEntry.getValue());
        };
        foreach(addMapping);

        return result;
    }

    @Override
    public <E> List<E> transform(Lists.Mapping<Entry<K, V>, E> mapping) {
        List<E> result = new ArrayList<>();
        Client<Map.Entry<K, V>> addTransform = entry -> result.add(mapping.map(entry));
        foreach(addTransform);

        return result;
    }

    @Override
    @NonNull
    public Set<V> values() {
        Set<V> result = new HashSet<>();
        Client<Map.Entry<K, V>> addValue = entry -> result.add(entry.getValue());
        foreach(addValue);

        return result;
    }

    @Override
    public Set<K> keys() {
        Set<K> result = new HashSet<>();
        Client<Map.Entry<K, V>> addKey = entry -> result.add(entry.getKey());
        foreach(addKey);

        return result;
    }

    @Override
    public void foreach(Client<Entry<K, V>> client) {
        for (Map.Entry<K, V> item : this.entrySet()) {
            client.supply(item);
        }
    }

    @Override
    public Map<V, K> invert() {
        Map<V, K> result = new HashMap<>();
        Client<Map.Entry<K, V>> addInvertedEntry = entry -> result.put(entry.getValue(), entry.getKey());
        foreach(addInvertedEntry);

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

        foreach(addMatches);

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

        foreach(addMatches);

        return result;
    }

    @Override
    public Map<K, V> nonNull() {
        Query<Map.Entry<K, V>> isNull = entry -> entry.getValue() == null;

        return reject(isNull);
    }

    public static <K, V> Map<K, V> create(Collection<K> keys, Collection<V> values) {
        if (keys.size() != values.size()) {
            throw new SizeMismatchException(Stringify.curlyFormat(
                    "Key collection (size: {keyCount}) must be of same size as value collection" +
                            " (size: {valueCount}).", keys.size(), values.size())
            );
        }

        Map<K, V> result = new HashMap<>();
        Iterator<K> keyIterator = keys.iterator();
        Iterator<V> valueIterator = values.iterator();

        while(keyIterator.hasNext()) {
            result.put(keyIterator.next(), valueIterator.next());
        }

        return result;
    }

    public HashMap<K, V> putList(List<K> keyList, List<V> valueList){
        HashMap<K, V> map = new HashMap<>();
        for(int i=0;i< keyList.size();i++){
            map.put(keyList.get(i), valueList.get(i));
        }

        return map;
    }


}
