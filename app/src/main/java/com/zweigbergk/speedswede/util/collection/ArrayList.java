package com.zweigbergk.speedswede.util.collection;

import com.zweigbergk.speedswede.util.Lists;
import com.zweigbergk.speedswede.util.methodwrapper.Client;
import com.zweigbergk.speedswede.util.methodwrapper.Query;

public class ArrayList<E> extends java.util.ArrayList<E> implements List<E> {

    @Override
    public Set<E> union(Iterable<E> other) {
        return null;
    }

    public Set<E> intersect(Iterable<E> other) {
        Set<E> result = new HashSet<>();

        for (E e : other) {
            if (contains(e)) {
                result.add(e);
            }
        }

        return result;
    }

    @Override
    public Set<E> difference(Iterable<E> other) {
        return null;
    }

    @Override
    public List<E> filter(Query<E> query) {
        List<E> result = new ArrayList<>();

        foreach(e -> {
            if (query.matches(e)) {
                result.add(e);
            }
        });

        return result;
    }

    @Override
    public List<E> reject(Query<E> query) {
        List<E> result = new ArrayList<>();

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
    public List<E> first(int amount) {
        List<E> result = new ArrayList<>();
        for (int i = 0; i < amount; ++i) {
            result.add(this.get(i));
        }

        return result;
    }

    @Override
    public <To> List<To> map(Lists.Mapping<E, To> mapping) {
        List<To> result = new ArrayList<>();

        for (E element : this) {
            result.add(mapping.map(element));
        }

        return result;
    }
}
