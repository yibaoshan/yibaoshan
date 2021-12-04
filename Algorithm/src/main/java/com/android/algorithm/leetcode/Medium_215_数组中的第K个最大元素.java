package com.android.algorithm.leetcode;

import org.junit.Test;

import java.util.Arrays;

public class Medium_215_数组中的第K个最大元素 {

    /**
     * 给定整数数组 nums 和整数 k，请返回数组中第 k 个最大的元素。
     * <p>
     * 请注意，你需要找的是数组排序后的第 k 个最大的元素，而不是第 k 个不同的元素。
     */

    @Test
    public void main() {
        int[] nums = new int[]{2,3,5,6,1};
        System.out.println(findKthLargest(nums,2));
    }

    /**
     * 执行结果：通过
     * 执行用时：2 ms, 在所有 Java 提交中击败了83.45%的用户
     * 内存消耗：38.8 MB, 在所有 Java 提交中击败了29.84%的用户
     */
    public int findKthLargest(int[] nums, int k) {
        if (nums==null||nums.length<k)return -1;
        Arrays.sort(nums);
        return nums[nums.length-k];
    }

}
