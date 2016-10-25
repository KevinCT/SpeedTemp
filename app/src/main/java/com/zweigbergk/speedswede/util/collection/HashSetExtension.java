package com.zweigbergk.speedswede.util.collection;

import com.zweigbergk.speedswede.util.methodwrapper.Client;
import com.zweigbergk.speedswede.util.methodwrapper.Query;

public class HashSetExtension<E> extends java.util.HashSet<E> implements SetExtension<E> {

    public HashSetExtension() {
        super();
    }

    public HashSetExtension(CollectionExtension<E> collection) {
        super(collection);
    }

    @Override
    public SetExtension<E> union(Iterable<E> other) {
        SetExtension<E> result = new HashSetExtension<>();
        Client<E> addToResult = this::add;
        this.foreach(addToResult);
        for (E item : other) {
            result.add(item);
        }

        return result;
    }

    public SetExtension<E> intersect(Iterable<E> other) {
        SetExtension<E> result = new HashSetExtension<>();

        for (E e : other) {
            if (contains(e)) {
                result.add(e);
            }
        }

        return result;
    }

    @Override
    public SetExtension<E> difference(Iterable<E> other) {
        SetExtension<E> result = new HashSetExtension<>();
        SetExtension<E> otherSet = Collections.asSet(other);

        Client<E> diff = e -> {
            if (!otherSet.contains(e)) {
                result.add(e);
            }
        };

        this.foreach(diff);

        return result;
    }

    @Override
    public void foreach(Client<E> client) {
        for (E item : this) {
            client.supply(item);
        }
    }

    @Override
    public SetExtension<E> filter(Query<E> query) {
        SetExtension<E> result = new HashSetExtension<>();

        foreach(e -> {
            if (query.matches(e)) {
                result.add(e);
            }
        });

        return result;
    }
}
