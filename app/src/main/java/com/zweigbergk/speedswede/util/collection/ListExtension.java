package com.zweigbergk.speedswede.util.collection;

import com.zweigbergk.speedswede.util.Lists;

public interface ListExtension<E> extends java.util.List<E>, CollectionExtension<E> {
    ListExtension<E> first(int amount);
    E getFirst();
    E getLast();
    <To> ListExtension<To> map(Lists.Mapping<E, To> mapping);

    void removeLast();
}
