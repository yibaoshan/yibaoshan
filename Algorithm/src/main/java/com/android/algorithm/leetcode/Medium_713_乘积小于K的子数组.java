package com.android.algorithm.leetcode;

import org.junit.Test;

import java.util.Arrays;

public class Medium_713_乘积小于K的子数组 {

    /**
     * 给定一个正整数数组 nums和整数 k 。
     * <p>
     * 请找出该数组内乘积小于 k 的连续的子数组的个数。
     */

    @Test
    public void main() {
        int[] nums = new int[]{10, 10, 10, 10};
        int k = 11;
        System.out.println(numSubarrayProductLessThanK(nums, k));
//        System.out.println(product(nums, 0, 2));
    }

    /**
     * 滑动窗口，思路：
     * 左右指针均从下标0开始循环，当右指针指向数组最右侧时终止，设prod为乘积，初始为1：
     * 1. prod每次乘上右指针所指的值，此时：
     *  1.1 若乘积大于k，乘积除以左指针所指的值(丢掉左指针的值)，同时左指针右移一位
     *  1.2 若乘积小于k，哎~ 符合题意，我们将结果累加res += right - left + 1，同时右指针继续右移，直到数组尽头~
     *
     * 这题的关键是：
     *  1. 乘积大于k，左指针右移；乘积小于k，右指针右移
     *  2. 理解当乘积小于k时，子数组的个数为什么是：res += right - left + 1而不是res++
     *
     * 执行结果：通过
     * 执行用时：4 ms, 在所有 Java 提交中击败了99.97%的用户
     * 内存消耗：45.1 MB, 在所有 Java 提交中击败了83.85%的用户
     */
    public int numSubarrayProductLessThanK(int[] nums, int k) {
        if (nums == null || k == 0 || k == 1) return 0;
        int left = 0, right = 0, prod = 1, res = 0;
        while (right < nums.length) {
            prod *= nums[right];
            while (prod >= k) {
                prod /= nums[left];
                left++;
            }
            res += right - left + 1;
            right++;
        }
        return res;
    }

    private int product(int[] nums, int start, int end) {
        int pro = nums[start];
        for (int i = ++start; i < end; i++) {
            pro *= nums[i];
        }
        return pro;
    }

    private boolean product(int[] nums, int start, int end, int k) {
        int pro = 1;
        for (int i = start; i <= end; i++) {
            pro *= nums[i];
            if (pro >= k) return false;
        }
        return true;
    }

}
