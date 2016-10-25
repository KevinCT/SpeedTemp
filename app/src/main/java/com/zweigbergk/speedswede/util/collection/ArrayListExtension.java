package com.zweigbergk.speedswede.util.collection;

import com.zweigbergk.speedswede.util.methodwrapper.Client;
import com.zweigbergk.speedswede.util.methodwrapper.Query;

@SuppressWarnings("Convert2streamapi")
public class ArrayListExtension<E> extends java.util.ArrayList<E> implements ListExtension<E> {

    @SuppressWarnings("unused")
    public SetExtension<E> intersect(Iterable<E> other) {
        SetExtension<E> result = new HashSetExtension<>();

        for (E e : other) {
            if (contains(e)) {
                result.add(e);
            }
        }

        return result;
    }

    public ListExtension<E> filter(Query<E> query) {
        ListExtension<E> result = new ArrayListExtension<>();

        foreach(e -> {
            if (query.matches(e)) {
                result.add(e);
            }
        });

        return result;
    }

    @SuppressWarnings("unused")
    public ListExtension<E> reject(Query<E> query) {
        ListExtension<E> result = new ArrayListExtension<>();

        foreach(e -> {
            if (!query.matches(e)) {
                result.add(e);
            }
        });

        return result;
    }

    @Override
    public void removeLast() {
        remove(size() - 1);
    }

    public void foreach(Client<E> client) {
        for(E e : this) {
            client.supply(e);
        }
    }

    public E getFirst() {
        if (size() == 0) {
            return null;
        }

        return get(0);
    }

    public E getLast() {
        if (size() == 0) {
            return null;
        }

        return get(size() - 1);
    }

    @Override
    public ListExtension<E> first(int amount) {
        ListExtension<E> result = new ArrayListExtension<>();
        for (int i = 0; i < amount; ++i) {
            result.add(this.get(i));
        }

        return result;
    }

    @Override
    public <To> ListExtension<To> map(Mapping<E, To> mapping) {
        ListExtension<To> result = new ArrayListExtension<>();

        for (E element : this) {
            result.add(mapping.map(element));
        }

        return result;
    }

    @Override
    public ListExtension<E> nonNull() {
        return reject(item -> item == null);
    }
}
