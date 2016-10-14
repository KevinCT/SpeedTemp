package com.zweigbergk.speedswede.util.collection;

import com.zweigbergk.speedswede.util.methodwrapper.Client;

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

    public void foreach(Client<E> client) {
        for(E e : this) {
            client.supply(e);
        }
    }

    @Override
    public List<E> first(int amount) {
        List<E> result = new ArrayList<>();
        for (int i = 0; i < amount; ++i) {
            result.add(this.get(i));
        }

        return result;
    }
}
