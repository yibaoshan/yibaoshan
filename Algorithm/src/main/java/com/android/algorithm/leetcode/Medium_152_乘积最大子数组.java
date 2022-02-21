package com.android.algorithm.leetcode;

import org.junit.Test;

import java.util.Arrays;

public class Medium_152_乘积最大子数组 {

    @Test
    public void main() {
//        int[] nums = new int[]{2, 3, -2, 4};
//        int[] nums = new int[]{-2, 3, -4};
//        int[] nums = new int[]{-2, 0, -4};
        int[] nums = new int[]{1, 2, -1, -2, 2, 1, -2, 1, 4, -5, 4};
        System.out.println(maxProduct(nums));
    }

    /**
     * 动态规划，创建最大值和最小值两个dp数组
     * 最大值公式=max(nums[i],minDP[i-1]*nums[i],maxDP[i-1]*nums[i])
     * 最小值公式=min(num[i],minDP[i-1]*nums[i],maxDP[i-1]*nums[i])
     * <p>
     * 执行结果：通过
     * 执行用时：3 ms, 在所有 Java 提交中击败了22.94%的用户
     * 内存消耗：41.2 MB, 在所有 Java 提交中击败了24.39%的用户
     */
    public int maxProduct(int[] nums) {
        int[] maxDP = new int[nums.length];
        int[] minDP = new int[nums.length];
        maxDP[0] = nums[0];
        minDP[0] = nums[0];
        for (int i = 1; i < nums.length; i++) {
            maxDP[i] = Math.max(Math.max(maxDP[i - 1] * nums[i], minDP[i - 1] * nums[i]), nums[i]);
            minDP[i] = Math.min(Math.min(minDP[i - 1] * nums[i], maxDP[i - 1] * nums[i]), nums[i]);
        }
        System.out.println(Arrays.toString(maxDP));
        System.out.println(Arrays.toString(minDP));
        Arrays.sort(maxDP);
        return maxDP[maxDP.length - 1];
    }

}
