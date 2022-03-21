package com.android.algorithm.contest;

import org.junit.Test;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

public class BiweeklyContest_74 {

    @Test
    public void test1() {
//        int[] nums = new int[]{3,2,3,2,2,2};
        int[] nums = new int[]{1, 2, 3, 4};
        System.out.println(divideArray(nums));
    }

    public boolean divideArray(int[] nums) {
        if (nums == null || nums.length % 2 != 0) return false;
        HashMap<Integer, Integer> hashMap = new HashMap<>();
        for (int num : nums) {
            if (hashMap.containsKey(num)) {
                hashMap.put(num, hashMap.get(num) + 1);
            } else hashMap.put(num, 1);
        }
        List<Integer> list = new ArrayList<>(hashMap.values());
        for (int cnt : list) {
            if (cnt % 2 != 0) return false;
        }
        return true;
    }

    @Test
    public void test2() {
        String text = "abdcdbc";
        String pattern = "ac";
//        System.out.println(maximumSubsequenceCount(text, pattern));
        System.out.println(maximumSubsequenceCount2(text, pattern));
    }

    public long maximumSubsequenceCount2(String text, String pattern) {
        StringBuilder stringBuilder = new StringBuilder(text);
        HashSet<String> hashSet = new HashSet<>();
        for (int i = 0; i < pattern.length(); i++) {
            for (int j = 0; j <= text.length(); j++) {
                stringBuilder.insert(j, pattern.charAt(i));
                if (hashSet.contains(stringBuilder.toString())) {
                    stringBuilder.delete(j, j + 1);
                    continue;
                }
                hashSet.add(stringBuilder.toString());
                System.out.println(stringBuilder.toString());
                stringBuilder.delete(j, j + 1);
            }
        }
        return max;
    }


    public long maximumSubsequenceCount(String text, String pattern) {
        StringBuilder stringBuilder = new StringBuilder(text);
        HashSet<String> hashSet = new HashSet<>();
        for (int i = 0; i < pattern.length(); i++) {
            for (int j = 0; j <= text.length(); j++) {
                stringBuilder.insert(j, pattern.charAt(i));
                if (hashSet.contains(stringBuilder.toString())) {
                    stringBuilder.delete(j, j + 1);
                    continue;
                }
                hashSet.add(stringBuilder.toString());
                cnt = 0;
                backtrack(stringBuilder.toString(), pattern, 0);
                max = Math.max(cnt, max);
                stringBuilder.delete(j, j + 1);
            }
        }
        return max;
    }

    private Deque<Character> deque = new ArrayDeque<>();
    private long cnt = 0;
    private long max = 0;

    private void backtrack(String text, String pattern, int start) {
        if (deque.size() == pattern.length()) {
            if (equals(pattern)) cnt++;
            return;
        }
        for (int i = start; i < text.length(); i++) {
            deque.addLast(text.charAt(i));
            backtrack(text, pattern, i + 1);
            deque.removeLast();
        }
    }

    private boolean equals(String pattern) {
        List<Character> list = new ArrayList<>(deque);
        for (int i = 0; i < pattern.length(); i++) {
            if (pattern.charAt(i) != list.get(i)) return false;
        }
        return true;
    }

    @Test
    public void test3() {
        int[] nums = new int[]{6, 58, 10, 84, 35, 8, 22, 64, 1, 78, 86, 71, 77};
        System.out.println(halveArray(nums));
    }

    public int halveArray(int[] nums) {
        if (nums == null || nums.length == 0) return 0;
        Arrays.sort(nums);
        double[] doubles = new double[nums.length];
        for (int i = 0; i < nums.length; i++) doubles[i] = nums[i] * 1.0;
        double sum = Arrays.stream(nums).sum();
        double halve = sum / 2.0;
        int cnt = 0;
        for (int i = doubles.length - 1; i >= 0; i--) {
            cnt++;
            double num = doubles[i] / 2.0;
            doubles[i] = num;
            sum -= num;
            if (sum <= halve) break;
        }
        return cnt;
    }

    @Test
    public void test4() {
    }

}
