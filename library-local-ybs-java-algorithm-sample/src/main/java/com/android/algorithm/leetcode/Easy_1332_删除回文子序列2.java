package com.android.algorithm.leetcode;

import org.junit.Test;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Queue;
import java.util.Stack;

public class Easy_1332_删除回文子序列2 {

    @Test
    public void test1() {
        int[] nums = new int[]{-100000, 0, 100000};
        System.out.println(countElements(nums));
    }

    public int countElements(int[] nums) {
        if (nums == null || nums.length < 3) return 0;
        Arrays.sort(nums);
        int sum = 0;
        for (int i = 1; i < nums.length; i++) {
            if (nums[i] > nums[0] && nums[i] < nums[nums.length - 1]) {
                sum++;
                System.out.println(nums[i]);
            }
        }
        return sum;
    }

    @Test
    public void test2() {
        int[] nums = new int[]{28, -41, 22, -8, -37, 46, 35, -9, 18, -6, 19, -26, -37, -10, -9, 15, 14, 31};
        System.out.println(Arrays.toString(nums));
        System.out.println(Arrays.toString(rearrangeArray(nums)));
    }

    public int[] rearrangeArray(int[] nums) {
        Queue<Integer> positive = new ArrayDeque<>();
        Queue<Integer> negative = new ArrayDeque<>();
        for (int i = 0; i < nums.length; i++) {
            if (nums[i] < 0) negative.add(nums[i]);
            else positive.add(nums[i]);
        }

        for (int i = 0; i < nums.length; i++) {
            if (i%2==0&&!positive.isEmpty())nums[i] = positive.poll();
            else if (!negative.isEmpty())nums[i] = negative.poll();
        }
        return nums;
    }

    @Test
    public void test3() {
        int[] nums = new int[]{1, 3, 5, 3};
        List<Integer> list = findLonely(nums);
        for (int i = 0; i < list.size(); i++) {
            System.out.println(list.get(i));
        }
    }

    public List<Integer> findLonely(int[] nums) {
        List<Integer> list = new ArrayList<>();
        if (nums == null || nums.length == 0) return list;
        if (nums.length == 1) {
            list.add(nums[0]);
            return list;
        }
        if (nums.length == 2) {
            if (Math.abs(nums[0] - nums[1]) > 1) {
                list.add(nums[0]);
                list.add(nums[1]);
            }
            return list;
        }
        Arrays.sort(nums);
        for (int i = 0; i < nums.length; i++) {
            if (i == 0 && Math.abs(nums[i] - nums[i + 1]) < 2) continue;
            if (i > 0 && i < nums.length - 1) {
                if (Math.abs(nums[i] - nums[i - 1]) < 2 || Math.abs(nums[i] - nums[i + 1]) < 2) continue;
            }
            if (i == nums.length - 1 && Math.abs(nums[i] - nums[i - 1]) < 2) continue;
            list.add(nums[i]);
        }
        return list;
    }

}
