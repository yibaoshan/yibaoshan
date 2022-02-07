package com.android.algorithm.leetcode;

import org.junit.Test;

import java.util.Arrays;

public class Medium_152_乘积最大子数组 {

    @Test
    public void main() {
        int[] nums = new int[]{2, 3, -2, 4};
        System.out.println(maxProduct(nums));
    }

    public int maxProduct(int[] nums) {
        int[] dp = new int[nums.length];
        dp[0] = nums[0];
        System.out.println(Arrays.toString(dp));
        for (int i = 1; i < nums.length; i++) {
            dp[i] = Math.max(dp[i - 1], dp[i - 1] * nums[i]);
            System.out.println(Arrays.toString(dp));
        }
        Arrays.sort(dp);
        return dp[dp.length - 1];
    }

}
