package com.ecstasy.hbmplus.Util;

import java.util.HashMap;
import java.util.Map;

public class Dictionary<T, U extends Comparable<U>> extends HashMap<T, U> {

    public Dictionary() {
        super();
    }

    public T smallest() {
        if (this.isEmpty()) {
            throw new IllegalStateException("The dictionary is empty");
        }

        U smallest = null;
        T index = null;

        for (Map.Entry<T, U> entry : this.entrySet()) {
            U value = entry.getValue();
            if (smallest == null || value.compareTo(smallest) < 0) {
                smallest = value;
                index = entry.getKey();
            }
        }

        return index;
    }

    public T biggest() {
        if (this.isEmpty()) {
            throw new IllegalStateException("The dictionary is empty");
        }

        U biggest = null;
        T index = null;

        for (Map.Entry<T, U> entry : this.entrySet()) {
            U value = entry.getValue();
            if (biggest == null || value.compareTo(biggest) > 0) {
                biggest = value;
                index = entry.getKey();
            }
        }

        return index;
    }
}
