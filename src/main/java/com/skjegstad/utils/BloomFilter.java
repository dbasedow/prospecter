package com.skjegstad.utils;

public interface BloomFilter<T> {
    public void add(T element);

    public void add(byte[] element);

    public boolean contains(T element);

    public boolean contains(byte[] element);
}
