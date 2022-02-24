package com.android.algorithm.leetcode;

import java.util.Arrays;

public class LeetCodeUtil {

    public static void print(int[] array) {
        System.out.println(Arrays.toString(array));
    }

    public static void print(int[][] array) {
        for (int[] ints : array)
            System.out.println(Arrays.toString(ints));
    }

}
