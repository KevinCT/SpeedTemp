package com.zweigbergk.speedswede.util.collection;

public class Arrays {
    public static <E> ArrayListExtension<E> asList(E... elements) {
        return Collections.asList(elements);
    }
}
