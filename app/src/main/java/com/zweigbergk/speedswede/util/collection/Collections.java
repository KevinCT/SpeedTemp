package com.zweigbergk.speedswede.util.collection;

public class Collections {

    public static <T> List<T> emptyList() {
        return new ArrayList<>();
    }

    public static <T> Set<T> emptySet() {
        return new HashSet<>();
    }
}
