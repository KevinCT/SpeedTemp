package com.zweigbergk.speedswede.util;

import com.zweigbergk.speedswede.util.collection.Arrays;
import com.zweigbergk.speedswede.util.collection.ListExtension;
import com.zweigbergk.speedswede.util.methodwrapper.Client;
import com.zweigbergk.speedswede.util.methodwrapper.Query;

import com.zweigbergk.speedswede.util.collection.ArrayListExtension;
import java.util.Collection;
import com.zweigbergk.speedswede.util.collection.HashMapExtension;
import java.util.HashSet;
import java.util.Iterator;

import com.zweigbergk.speedswede.util.collection.MapExtension;
import java.util.Set;

public class Lists {
    public static final String TAG = Lists.class.getSimpleName().toUpperCase();

    public static <E> void forEach(Iterable<E> collection, Client<E> client) {
        for (E element : collection) {
            client.supply(element);
        }
    }

    public static <E> void forEach(Iterator<E> iterator, Client<E> client) {
        while (iterator.hasNext()) {
            client.supply(iterator.next());
        }
    }

    public static <K, V> void forEach(MapExtension<K, V> map, Client<MapExtension.Entry<K, V>> client) {
        for (MapExtension.Entry<K, V> entry : map.entrySet()) {
            client.supply(entry);
        }
    }

    public static <E> void forEach(E[] array, Client<E> client) {
        forEach(Arrays.asList(array), client);
    }

    public static <E> ListExtension<E> filter(Iterable<E> collection, Query<E> query) {
        ListExtension<E> result = new ArrayListExtension<>();
        forEach(collection, e -> {
            if (query.matches(e)) {
                result.add(e);
            }
        });

        return result;
    }

    public static <E> ListExtension<E> filter(E[] collection, Query<E> query) {
        return filter(Arrays.asList(collection), query);
    }

    public static <From, To> ListExtension<To> map(Iterable<From> collection, Mapping<From, To> tool) {
        ListExtension<To> result = new ArrayListExtension<>();

        forEach(collection, e -> result.add(tool.map(e)));

        return result;
    }

    public static <K, V> MapExtension<K, V> map(MapExtension<?, ?> source, EntryMapping<K, V> tool) {
        MapExtension<K, V> result = new HashMapExtension<>();

        forEach(source, entry -> {
            MapExtension.Entry<K, V> mapping = tool.map(entry);
            result.put(mapping.getKey(), mapping.getValue());
        });

        return result;
    }


    public static <E> Set<E> union(Collection<E> first, Collection<E> second) {
        Set<E> result = new HashSet<>();

        if (first != null) {
            result.addAll(first);
        }

        if (second != null) {
            result.addAll(second);
        }

        return result;
    }


    public interface Mapping<From, To> {
        To map(From object);
    }

    public interface EntryMapping<K, V> {
        MapExtension.Entry<K, V> map(MapExtension.Entry entry);
    }
}
