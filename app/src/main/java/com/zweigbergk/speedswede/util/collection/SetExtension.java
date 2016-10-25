package com.zweigbergk.speedswede.util.collection;


import com.zweigbergk.speedswede.util.methodwrapper.Query;

public interface SetExtension<E> extends java.util.Set<E>, CollectionExtension<E> {
    SetExtension<E> union(Iterable<E> other);
    SetExtension<E> intersect(Iterable<E> other);
    SetExtension<E> difference(Iterable<E> other);
    SetExtension<E> filter(Query<E> query);
}
