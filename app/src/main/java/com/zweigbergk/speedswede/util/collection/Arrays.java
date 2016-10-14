package com.zweigbergk.speedswede.util.collection;

public class Arrays {

    public static <E> List<E> asList(E... elements) {
        List<E> result = new ArrayList<>();
        for (E element : elements) {
            result.add(element);
        }

        return result;
    }
}
