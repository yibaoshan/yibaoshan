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
        int[] nums = new int[]{10, 5, 2, 6};
        int k = 100;
        System.out.println(numSubarrayProductLessThanK(nums, k));
//        System.out.println(product(nums, 0, 2));
    }

    /**
     * 执行结果：通过
     * 执行用时：4 ms, 在所有 Java 提交中击败了99.97%的用户
     * 内存消耗：45.1 MB, 在所有 Java 提交中击败了83.85%的用户
     */
    public int numSubarrayProductLessThanK(int[] nums, int k) {
        if(k == 0 || k == 1) return 0;
        int l = 0;
        int prod = 1;
        int res = 0;
        for(int r = 0; r < nums.length; r++){
            prod *= nums[r];
            while(prod >= k){
                prod /= nums[l++];
            }
            res += r - l + 1;
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
