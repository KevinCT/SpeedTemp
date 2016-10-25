package com.zweigbergk.speedswede.util.collection;

import com.zweigbergk.speedswede.util.methodwrapper.Client;

public interface CollectionExtension<E> extends java.util.Collection<E> {
    void foreach(Client<E> client);

    interface Mapping<From, To> {
        To map(From object);
    }

    interface EntryMapping<K, V> {
        <X, Y> MapExtension.Entry<X, Y> map(MapExtension.Entry<K, V> entry);
    }
}
