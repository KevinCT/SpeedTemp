package com.zweigbergk.speedswede.util.collection;

import com.zweigbergk.speedswede.util.methodwrapper.Query;

public interface Set<E> extends java.util.Set<E>, Collection<E> {
    Set<E> filter(Query<E> query);
    Set<E> reject(Query<E> query);
}
