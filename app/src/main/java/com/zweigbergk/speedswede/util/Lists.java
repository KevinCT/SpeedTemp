package com.zweigbergk.speedswede.util;

import com.zweigbergk.speedswede.util.collection.Arrays;
import com.zweigbergk.speedswede.util.methodwrapper.Client;
import com.zweigbergk.speedswede.util.methodwrapper.EntryAssertion;
import com.zweigbergk.speedswede.util.methodwrapper.Query;

import com.zweigbergk.speedswede.util.collection.ArrayList;
import java.util.Collection;
import com.zweigbergk.speedswede.util.collection.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import com.zweigbergk.speedswede.util.collection.List;
import com.zweigbergk.speedswede.util.collection.Map;
import java.util.Random;
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

    public static <K, V> void forEach(Map<K, V> map, Client<Map.Entry<K, V>> client) {
        for (Map.Entry<K, V> entry : map.entrySet()) {
            client.supply(entry);
        }
    }

    public static <E> void forEach(E[] array, Client<E> client) {
        forEach(Arrays.asList(array), client);
    }

    public static <E> List<E> filter(Iterable<E> collection, Query<E> query) {
        List<E> result = new ArrayList<>();
        forEach(collection, e -> {
            if (query.matches(e)) {
                result.add(e);
            }
        });

        return result;
    }

    public static <E> List<E> filter(E[] collection, Query<E> query) {
        return filter(Arrays.asList(collection), query);
    }

    public static <E> List<E> reject(Iterable<E> collection, Query<E> query) {
        List<E> result = new ArrayList<>();
        forEach(collection, e -> {
            if (!query.matches(e)) {
                result.add(e);
            }
        });

        return result;
    }

    public static <From, To> List<To> map(Iterable<From> collection, Mapping<From, To> tool) {
        List<To> result = new ArrayList<>();

        forEach(collection, e -> result.add(tool.map(e)));

        return result;
    }

    public static <K, V> Map<K, V> map(Map<?, ?> source, EntryMapping<K, V> tool) {
        Map<K, V> result = new HashMap<>();

        forEach(source, entry -> {
            Map.Entry<K, V> mapping = tool.map(entry);
            result.put(mapping.getKey(), mapping.getValue());
        });

        return result;
    }

    public static <K, V> Map<K, V> reject(Map<K, V> source, EntryAssertion<K, V> assertion) {
        Map<K, V> result = new HashMap<>();

        forEach(source, entry -> {
            if (!assertion.accepts(entry)) {
                result.put(entry.getKey(), entry.getValue());
            }
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

    public static <E> E randomPick(E[] array) {
        return randomPick(Arrays.asList(array));
    }

    public static <E> E randomPick(List<E> list) {
        Random random = new Random();
        random.nextInt();
        random.nextInt();

        int i = random.nextInt(list.size());
        return list.get(i);
    }

    /**Adds all elements from one collection to another.
     *
     * @param target Collection for which to add elements
     * @param source Collection from which to take elements
     *
     */
    public static void addAll(Collection target, Collection source) {
        forEach(source, target::add);
    }

    public static <E> List<E> getFirstElements(List<E> collection, int value) {
        List<E> result = new ArrayList<>();

        for (int i = 0; i < value; ++i) {
            result.add(collection.get(i));
        }

        return result;
    }

    public static <E> E getLastElement(List<E> collection) {
        return collection.size() != 0 ?
                collection.get(collection.size() - 1) : null;
    }


    public interface Mapping<From, To> {
        To map(From object);
    }

    public interface EntryMapping<K, V> {
        Map.Entry<K, V> map(Map.Entry entry);
    }

    public interface FilterMapping<K, V> {
        Map.Entry<K, V> map(Map.Entry<K, V> entry);
    }

    public interface EntryToTuple<K, V> {
        Tuple<K, V> map(Map.Entry<?, ?> entry);
    }
}
