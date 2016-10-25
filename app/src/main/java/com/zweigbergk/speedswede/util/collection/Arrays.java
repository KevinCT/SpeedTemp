package com.zweigbergk.speedswede.util.collection;

public class Arrays {
    @SafeVarargs
    public static <E> ArrayListExtension<E> asList(E... elements) {
        return Collections.asList(elements);
    }
}
