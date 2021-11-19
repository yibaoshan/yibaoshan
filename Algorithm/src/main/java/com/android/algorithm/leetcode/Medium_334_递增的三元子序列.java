package com.android.algorithm.leetcode;

import org.junit.Test;

public class Medium_334_递增的三元子序列 {

    /**
     * 给你一个整数数组 nums ，判断这个数组中是否存在长度为 3 的递增子序列。
     * <p>
     * 如果存在这样的三元组下标 (i, j, k) 且满足 i < j < k ，使得 nums[i] < nums[j] < nums[k] ，返回 true ；否则，返回 false 。
     * <p>
     * 来源：力扣（LeetCode）
     * 链接：https://leetcode-cn.com/problems/increasing-triplet-subsequence
     * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
     */

    @Test
    public void main() {
//        int[] nums = new int[]{1, 0, 3, -1, 5};
        int[] nums = new int[]{1, 1, 1, 1};
        System.out.println(increasingTriplet(nums));
    }

    /**
     * 思路：最小值n1，次之n2，加两个判断条件，能走到else说明肯定大于n1和n2且n1<n2
     * 执行结果：通过
     * 执行用时：2 ms, 在所有 Java 提交中击败了80.66%的用户
     * 内存消耗：79.1 MB, 在所有 Java 提交中击败了40.94%的用户
     */
    public boolean increasingTriplet(int[] nums) {
        if (nums == null || nums.length < 3) return false;
        int n1 = Integer.MAX_VALUE, n2 = n1;
        for (int i = 0; i < nums.length; i++) {
            int cur = nums[i];
            if (cur <= n1) {
                n1 = cur;
            } else if (cur <= n2) {
                n2 = cur;
            } else return true;
        }
        return false;
    }

}
