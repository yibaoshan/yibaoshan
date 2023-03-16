package com.android.algorithm.leetcode;

import org.junit.Test;

public class Medium_1567_乘积为正数的最长子数组长度 {

    @Test
    public void main() {
//        int[] nums = new int[]{1, -2, -3, 4};
//        int[] nums = new int[]{-1, 2, 2};
//        int[] nums = new int[]{0, 1, -2, -3, -4};
//        int[] nums = new int[]{-1, -2, -3, 0, 1};
        int[] nums = new int[]{1, 2, 3, 5, -6, 4, 0, 10};
        System.out.println(getMaxLen(nums));
    }

    /**
     * 贪心，思路：遍历数组，记录正负值的个数
     * 1. 若负数个数为偶数，则最大长度为正数个数+负数个数
     * 2. 若负数个数为奇数，不要第一次出现负数的位置了，抛弃它即可让负数个数为偶数，当前下标减掉负数第一次出现的下标，中间即为最大长度，
     * 执行结果：通过
     * 执行用时：3 ms, 在所有 Java 提交中击败了99.84%的用户
     * 内存消耗：56.7 MB, 在所有 Java 提交中击败了15.09%的用户
     */
    public int getMaxLen(int[] nums) {
        int max = 0, start = -1, positive = 0, negative = 0;
        for (int i = 0; i < nums.length; i++) {
            int num = nums[i];
            if (num == 0) {
                positive = 0;
                negative = 0;
                start = -1;
            } else if (num > 0) {
                positive++;
            } else {
                if (start == -1) start = i;
                negative++;
            }
            if (negative % 2 == 0) {
                max = Math.max(max, positive + negative);
            } else {
                max = Math.max(max, i - start);
            }
        }
        return max;
    }

}
