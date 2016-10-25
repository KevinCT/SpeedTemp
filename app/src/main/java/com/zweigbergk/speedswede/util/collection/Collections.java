package com.zweigbergk.speedswede.util.collection;

import java.util.Iterator;

public class Collections {

    public static <T> ListExtension<T> emptyList() {
        return new ArrayListExtension<>();
    }

    public static <T> SetExtension<T> emptySet() {
        return new HashSetExtension<>();
    }

    public static <T> ArrayListExtension<T> asList(T... objects) {
        ArrayListExtension<T> list = new ArrayListExtension<>();
        for (T object : objects) {
            list.add(object);
        }

        return list;
    }

    public static <T> ListExtension<T> asList(Iterator<T> iterator) {
        ListExtension<T> list = new ArrayListExtension<>();

        while(iterator.hasNext()) {
            list.add(iterator.next());
        }

        return list;
    }

    public static <E> SetExtension<E> asSet(Iterable<E> iterable) {
        SetExtension<E> result = new HashSetExtension<>();
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
