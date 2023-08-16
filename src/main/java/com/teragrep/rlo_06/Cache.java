package com.teragrep.rlo_06;

interface Cache<T> {
    T take();
    void put(T t);
}
