package com.cazsius.deathquotes.impl;

import java.util.LinkedHashSet;

public class LimitedSet<T> {
    private final LinkedHashSet<T> set = new LinkedHashSet<>();
    private final int size;
    private final boolean clearOnLimit;

    public LimitedSet(int size, boolean clearOnLimit) {
        this.size = size;
        this.clearOnLimit = clearOnLimit;
    }

    public int getSize() {
        return this.size;
    }

    private T first() {
        return set.iterator().next();
    }

    synchronized public boolean add(T item) {
        if (set.size() >= size) {
            if (clearOnLimit) {
                set.clear();
            } else {
                set.remove(first());
            }
        }
        return set.add(item);
    }

    synchronized public boolean contains(T item) {
        return set.contains(item);
    }
}
