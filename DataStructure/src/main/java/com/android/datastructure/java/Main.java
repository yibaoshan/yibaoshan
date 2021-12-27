package com.android.datastructure.java;

import org.junit.Test;

public class Main {

    @Test
    public void main() {
        LRUCache<Integer,Integer> lruCache = new LRUCache<>(5);
        for (int i = 0; i < 10; i++) {
            lruCache.put(i,i);
            System.out.println(lruCache.size());
        }
    }

}
