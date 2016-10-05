package com.zweigbergk.speedswede.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Lists {
    public static <E> void forEach(Iterable<E> collection, Client<E> client) {
        for (E element : collection)
            client.supply(element);
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

    public static <E> List<E> getFirstElements(List<E> collection, int value) {
        List<E> result = new ArrayList<>();

        for (int i = 0; i < value; ++i) {
            result.add(collection.get(i));
        }

        return result;
    }

    public static <E> E getLast(List<E> collection) {
        return collection.size() != 0 ?
                collection.get(collection.size() - 1) : null;
    }
}
