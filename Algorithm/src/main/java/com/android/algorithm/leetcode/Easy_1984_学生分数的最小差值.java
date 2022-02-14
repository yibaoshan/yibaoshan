package com.android.algorithm.leetcode;

import org.junit.Test;

import java.util.Arrays;

public class Easy_1984_学生分数的最小差值 {

    @Test
    public void main() {
        int[] nums = new int[]{9, 4, 1, 7};
        int k = 2;
        System.out.println(minimumDifference(nums, k));
    }

    /**
     * 执行结果：通过
     * 执行用时：4 ms, 在所有 Java 提交中击败了100.00%的用户
     * 内存消耗：41.3 MB, 在所有 Java 提交中击败了11.04%的用户
     */
    public int minimumDifference(int[] nums, int k) {
        if (nums == null || nums.length == 0 || k < 2) return 0;
        Arrays.sort(nums);
        int min = Integer.MAX_VALUE;
        for (int i = 0; i <= nums.length - k; i++) {
            min = Math.min(min, nums[i + k - 1] - nums[i]);
        }
        return min;
    }

}
