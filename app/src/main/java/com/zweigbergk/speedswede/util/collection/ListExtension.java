package com.zweigbergk.speedswede.util.collection;

public interface ListExtension<E> extends java.util.List<E>, CollectionExtension<E> {
    ListExtension<E> first(int amount);
    E getFirst();
    E getLast();
    <To> ListExtension<To> map(Mapping<E, To> mapping);
    <K, V> MapExtension<K, V> toMap(Mapping<E, MapExtension.Entry<K, V>> mapping);

    ListExtension<E> nonNull();

    void removeLast();
}
