package com.android.algorithm.leetcode;

import org.junit.Test;

import java.util.Arrays;

public class Easy_27_移除元素 {

    @Test
    public void main() {
        int[] nums = new int[]{3,2,2,3};
        int val = 3;
        System.out.println(removeElement(nums, val));
        System.out.println(Arrays.toString(nums));
    }

    /**
     * 暴力循环
     * 执行结果：通过
     * 执行用时：0 ms, 在所有 Java 提交中击败了100.00%的用户
     * 内存消耗：40 MB, 在所有 Java 提交中击败了14.59%的用户
     */
    public int removeElement(int[] nums, int val) {
        int cnt = 0;
        for (int i = 0; i < nums.length - cnt; i++) {
            if (nums[i] == val) {
                for (int j = i; j < nums.length - 1 - cnt; j++) {
                    nums[j] = nums[j + 1];
                }
                i--;
                cnt++;
            }
        }
        return nums.length - cnt;
    }

}
