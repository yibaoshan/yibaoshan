package com.android.algorithm.leetcode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LeetCodeUtil {

    public static void print(int[] array) {
        System.out.println(Arrays.toString(array));
    }

    public static void print(int[][] array) {
        for (int[] ints : array)
            System.out.println(Arrays.toString(ints));
    }

    public static void print(char[][] array) {
        for (char[] ints : array)
            System.out.println(Arrays.toString(ints));
    }

    public static int[][] parseStringToIntTwoDArray(String str) {
        Object[][] objects = buildTwoDArray(str);
        int[][] ints = new int[objects.length][];
        for (int i = 0; i < objects.length; i++) {
            int[] ints1 = new int[objects[i].length];
            for (int j = 0; j < objects[i].length; j++) {
                ints1[j] = Integer.parseInt(objects[i][j].toString());
            }
            ints[i] = ints1;
        }
        return ints;
    }

    public static long[][] parseStringToLongTwoDArray(String str) {
        Object[][] objects = buildTwoDArray(str);
        long[][] ints = new long[objects.length][];
        for (int i = 0; i < objects.length; i++) {
            long[] ints1 = new long[objects[i].length];
            for (int j = 0; j < objects[i].length; j++) {
                ints1[j] = Integer.parseInt(objects[i][j].toString());
            }
            ints[i] = ints1;
        }
        return ints;
    }

    public static Object[][] buildTwoDArray(String str) {
        List<List<Character>> lists = new ArrayList<>();
        for (int i = 0; i < str.length(); i++) {
            List<Character> list = new ArrayList<>();
            while (str.charAt(i) != ']') {
                if (str.charAt(i) == ',' || str.charAt(i) == '[') {
                    i++;
                    continue;
                }
                list.add(str.charAt(i));
                i++;
            }
            if (!list.isEmpty()) lists.add(list);
        }
        return listToTwoArray(lists);
    }

    public static <T> List<T> twoArrayToList(T[][] twoDArray) {
        List<T> list = new ArrayList<T>();
        for (T[] array : twoDArray) {
            list.addAll(Arrays.asList(array));
        }
        return list;
    }

    public static <T> Object[][] listToTwoArray(List<List<T>> list) {
        Object[][] objects = new Object[list.size()][];
        for (int i = 0; i < list.size(); i++) {
            objects[i] = list.get(i).toArray(new Object[]{});
        }
        return objects;
    }

}
