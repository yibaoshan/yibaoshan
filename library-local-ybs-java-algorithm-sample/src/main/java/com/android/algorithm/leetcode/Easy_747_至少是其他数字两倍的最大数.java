package com.android.algorithm.leetcode;

import org.junit.Test;

public class Easy_747_至少是其他数字两倍的最大数 {

    /**
     * 给你一个整数数组 nums ，其中总是存在 唯一的 一个最大整数 。
     * <p>
     * 请你找出数组中的最大元素并检查它是否 至少是数组中每个其他数字的两倍 。如果是，则返回 最大元素的下标 ，否则返回 -1 。
     * <p>
     * <p>
     * <p>
     * 来源：力扣（LeetCode）
     * 链接：https://leetcode-cn.com/problems/largest-number-at-least-twice-of-others
     * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
     */

    @Test
    public void main() {
        int[] nums = new int[]{1,2,3,4,6,9,23};
        System.out.println(dominantIndex(nums));
    }

    public int dominantIndex(int[] nums) {
        if (nums == null || nums.length == 0) return -1;
        if (nums.length == 1) return 0;
        int first = Integer.MIN_VALUE, second = Integer.MIN_VALUE, firstIndex = -1;
        for (int i = 0; i < nums.length; i++) {
            int cur = nums[i];
            if (cur > first) {
                second = Math.max(first, second);
                first = cur;
                firstIndex = i;
            } else if (cur > second) {
                second = cur;
            }
        }
        if (first >= second * 2) return firstIndex;
        return -1;
    }

}
