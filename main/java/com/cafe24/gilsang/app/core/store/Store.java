package com.cafe24.app.sample.core.store;

public interface Store<T> {

    T get(String key);

    void put(String key, T value);

    boolean contains(String key);
}
