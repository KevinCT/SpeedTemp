package com.zweigbergk.speedswede.util.collection;

import java.util.Iterator;

public class Arrays {
    @SafeVarargs
    public static <E> ArrayListExtension<E> asList(E... elements) {
        return Collections.asList(elements);
    }

    public static <E> ArrayListExtension<E> asList(Iterator<E> iterator) {
        ArrayListExtension<E> result = new ArrayListExtension<>();

        while(iterator.hasNext()) {
            result.add(iterator.next());
        }

        return result;
    }

    public static <E> ArrayListExtension<E> asList(Iterable<E> iterable) {
        ArrayListExtension<E> result = new ArrayListExtension<>();

        for (E e : iterable) {
            result.add(e);
        }

        return result;
    }
}
