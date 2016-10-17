package com.zweigbergk.speedswede.util.collection;

import com.zweigbergk.speedswede.util.methodwrapper.Client;

public interface Collection<E> extends java.util.Collection<E> {
    Collection<E> union(Iterable<E> other);
    Collection<E> intersect(Iterable<E> other);
    Collection<E> difference(Iterable<E> other);

    void foreach(Client<E> client);
}
