package com.zweigbergk.speedswede.util.collection;

import com.zweigbergk.speedswede.util.methodwrapper.Client;

public interface CollectionExtension<E> extends java.util.Collection<E> {
    CollectionExtension<E> union(Iterable<E> other);
    CollectionExtension<E> intersect(Iterable<E> other);
    CollectionExtension<E> difference(Iterable<E> other);

    void foreach(Client<E> client);
}
