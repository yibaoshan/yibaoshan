package com.ybs.xv6;

import java.util.HashMap;

public class Test {

    @org.junit.Test
    public void main() {
        System.out.println("Hello World!");
        int max = 0;
        for (int i = 0; i < 1000; i++) {
            int diff = hashMaxDiff();
            if (diff > max) max = diff;
        }
        System.out.println("max diff=" + max);

        HashMap<Integer, Integer> map = new HashMap<>();

        for (int i = 0; i < 30; i++) {
            int hash = hash(i);
            if (map.containsKey(hash)) map.put(hash, map.get(hash) + 1);
            else map.put(hash, 1);
        }
        for (Integer key : map.keySet()) {
            System.out.println("key=" + key + ", value=" + map.get(key));
        }
    }

    private int hashMaxDiff() {
        HashMap<Integer, Integer> map = new HashMap<>();
        for (int i = 0; i < 1000; i++) {
            int hash = hash((int) (Math.random() * 1000) + 1);
            if (map.containsKey(hash)) map.put(hash, map.get(hash) + 1);
            else map.put(hash, 1);
        }

        int max = Integer.MIN_VALUE, min = Integer.MAX_VALUE;
        for (Integer key : map.keySet()) {
            int v = map.get(key);
            if (v > max) max = v;
            if (v < min) min = v;
//            System.out.println("key=" + key + ", value=" + v);
        }
//        System.out.println("max diff=" + (max - min));
        return max - min;
    }

    private int hash(int blockno) {
//        int h = dev ^ blockno;
//        return blockno(13 - 1);
        return blockno % 13;
    }
}
