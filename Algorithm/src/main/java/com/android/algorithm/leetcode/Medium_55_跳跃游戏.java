package com.android.algorithm.leetcode;

import org.junit.Test;

public class Medium_55_跳跃游戏 {

    /**
     * 给定一个非负整数数组 nums ，你最初位于数组的 第一个下标 。
     * <p>
     * 数组中的每个元素代表你在该位置可以跳跃的最大长度。
     * <p>
     * 判断你是否能够到达最后一个下标。
     */

    @Test
    public void main() {
//        int[] nums = new int[]{2, 3, 1, 1, 4};
//        int[] nums = new int[]{3, 2, 2, 0, 4};
        int[] nums = new int[]{2, 0, 0};
        System.out.println(canJump(nums));
    }

    /**
     * 思路：从后向前遍历，
     * 1. 如果所有元素都不为0， 那么一定可以跳到最后；
     * 2. 从后往前遍历，如果遇到nums[i] = 0，就找i前面的元素j，使得nums[j] > i - j。如果找不到，则不可能跳跃到num[i+1]，返回false。
     * 这里考虑一种特殊情况，刚好可以跳到最后一位时结果同样为true
     * <p>
     * 执行结果：通过
     * 执行用时：2 ms, 在所有 Java 提交中击败了95.36%的用户
     * 内存消耗：40 MB, 在所有 Java 提交中击败了9.97%的用户
     */
    public boolean canJump(int[] nums) {
        if (nums == null || nums.length == 0) return false;
        if (nums.length == 1) return true;
        for (int i = nums.length - 1; i >= 0; i--) {
            out:
            if (nums[i] == 0) {
                for (int j = i; j >= 0; j--) {
                    if (nums[j] > i - j || nums[j] == i - j && i == nums.length - 1) {
                        i = j;
                        break out;
                    }
                }
                return false;
            }
        }
        return true;
    }

}
