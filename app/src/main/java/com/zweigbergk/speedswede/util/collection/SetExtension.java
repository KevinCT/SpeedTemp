package com.zweigbergk.speedswede.util.collection;

import com.zweigbergk.speedswede.util.methodwrapper.Query;

public interface SetExtension<E> extends java.util.Set<E>, CollectionExtension<E> {
    SetExtension<E> filter(Query<E> query);
}
