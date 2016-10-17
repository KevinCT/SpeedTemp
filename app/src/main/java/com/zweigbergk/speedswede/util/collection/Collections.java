package com.zweigbergk.speedswede.util.collection;

import java.util.Iterator;

public class Collections {

    public static <T> List<T> emptyList() {
        return new ArrayList<>();
    }

    public static <T> Set<T> emptySet() {
        return new HashSet<>();
    }

    public static <T> List<T> asList(T... objects) {
        List<T> list = new ArrayList<>();
        for (T object : objects) {
            list.add(object);
        }

        return list;
    }

    public static <T> List<T> asList(Iterator<T> iterator) {
        List<T> list = new ArrayList<>();

        while(iterator.hasNext()) {
            list.add(iterator.next());
        }

        return list;
    }

    public static <E> com.zweigbergk.speedswede.util.collection.Set<E> asSet(Iterable<E> iterable) {
        com.zweigbergk.speedswede.util.collection.Set<E> result = new com.zweigbergk.speedswede.util.collection.HashSet<>();
        for (E item : iterable) {
            result.add(item);
        }

        return result;
    }

    static class SizeMismatchException extends RuntimeException {
        SizeMismatchException(String message) {
            super(message);
        }
    }
}
