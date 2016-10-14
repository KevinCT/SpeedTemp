package com.zweigbergk.speedswede.util.collection;

public class Collections {

    public static <T> List<T> emptyList() {
        return new ArrayList<>();
    }

    public static <T> Set<T> emptySet() {
        return new HashSet<>();
    }

    public static <E> com.zweigbergk.speedswede.util.collection.Set<E> asSet(Iterable<E> iterable) {
        com.zweigbergk.speedswede.util.collection.Set<E> result = new com.zweigbergk.speedswede.util.collection.HashSet<>();
        for (E item : iterable) {
            result.add(item);
        }

        return result;
    }
}
