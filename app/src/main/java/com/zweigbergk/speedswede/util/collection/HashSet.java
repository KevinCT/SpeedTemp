package com.zweigbergk.speedswede.util.collection;

import com.zweigbergk.speedswede.util.methodwrapper.Client;
import com.zweigbergk.speedswede.util.methodwrapper.Query;

public class HashSet<E> extends java.util.HashSet<E> implements Set<E> {

    public HashSet() {
        super();
    }

    public HashSet(Collection<E> collection) {
        super(collection);
    }

    @Override
    public Set<E> union(Iterable<E> other) {
        Set<E> result = new HashSet<>();
        Client<E> addToResult = this::add;
        this.foreach(addToResult);
        for (E item : other) {
            result.add(item);
        }

        return result;
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
        Set<E> result = new HashSet<>();
        Set<E> otherSet = Collections.asSet(other);

        Client<E> diff = e -> {
            if (!otherSet.contains(e)) {
                result.add(e);
            }
        };

        this.foreach(diff);

        return result;
    }

    @Override
    public Set<E> reject(Query<E> query) {
        Set<E> result = new HashSet<>();

        foreach(e -> {
            if (!query.matches(e)) {
                result.add(e);
            }
        });

        return result;
    }

    @Override
    public void foreach(Client<E> client) {
        for (E item : this) {
            client.supply(item);
        }
    }

    @Override
    public Set<E> filter(Query<E> query) {
        Set<E> result = new HashSet<>();

        foreach(e -> {
            if (query.matches(e)) {
                result.add(e);
            }
        });

        return result;
    }
}
