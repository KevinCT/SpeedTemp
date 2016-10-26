package com.zweigbergk.speedswede.util.collection;

import com.zweigbergk.speedswede.util.methodwrapper.Client;

import java.util.Map;


public interface CollectionExtension<E> extends java.util.Collection<E> {
    void foreach(Client<E> client);

    interface Mapping<From, To> {
        To map(From object);
    }

    interface EntryMapping<K, V> {
        <X, Y> Map.Entry<X, Y> map(Map.Entry<K, V> entry);
    }
}
