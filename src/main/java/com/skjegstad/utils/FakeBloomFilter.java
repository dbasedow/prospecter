package com.skjegstad.utils;

public class FakeBloomFilter<T> implements BloomFilter<T> {
    @Override
    public void add(T element) {

    }

    @Override
    public void add(byte[] element) {

    }

    @Override
    public boolean contains(T element) {
        return true;
    }

    @Override
    public boolean contains(byte[] element) {
        return true;
    }
}
