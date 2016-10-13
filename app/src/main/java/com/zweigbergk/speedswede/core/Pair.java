package com.zweigbergk.speedswede.core;

public class Pair<T> {

    private final T first, second;

    public Pair(T first, T second) {
        this.first = first;
        this.second = second;
    }

    public T getFirst() {
        return this.first;
    }

    public T getSecond() {
        return this.second;
    }

    public boolean equals(Pair<T> other) {
        if (other == null || other.getFirst() == null || other.getSecond() == null) {
            return false;
        }
        if (!(other.getFirst().getClass().equals(this.first.getClass()) &&
                other.getSecond().getClass().equals(this.second.getClass()))) {
            return false;
        }
        return other.getFirst().equals(this.first) && other.getSecond().equals(this.second);
    }
}
