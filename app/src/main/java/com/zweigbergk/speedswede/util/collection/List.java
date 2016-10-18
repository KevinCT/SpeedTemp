package com.zweigbergk.speedswede.util.collection;

import com.zweigbergk.speedswede.util.Lists;
import com.zweigbergk.speedswede.util.methodwrapper.Query;

public interface List<E> extends java.util.List<E>, Collection<E> {
    List<E> first(int amount);
    E getFirst();
    E getLast();
    <To> List<To> map(Lists.Mapping<E, To> mapping);

    List<E> filter(Query<E> query);
    List<E> reject(Query<E> query);
    void removeLast();
}
