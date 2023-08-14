package com.teragrep.rlo_06;

public interface Cache<T> {
    T take();
    void put(T t);
}
