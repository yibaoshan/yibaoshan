package com.android.algorithm.leetcode;

import org.junit.Test;

import java.util.Arrays;

public class Medium_413_等差数列划分 {

    /**
     * 如果一个数列 至少有三个元素 ，并且任意两个相邻元素之差相同，则称该数列为等差数列。
     * <p>
     * 例如，[1,3,5,7,9]、[7,7,7,7] 和 [3,-1,-5,-9] 都是等差数列。
     * 给你一个整数数组 nums ，返回数组 nums 中所有为等差数组的 子数组 个数。
     * <p>
     * 子数组 是数组中的一个连续序列。
     * <p>
     * <p>
     * <p>
     * 来源：力扣（LeetCode）
     * 链接：https://leetcode-cn.com/problems/arithmetic-slices
     * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
     */

    @Test
    public void main() {
//        int[] nums = new int[]{1, 2, 3, 4};
        int[] nums = new int[]{3, -1, -5, -9};
        System.out.println(numberOfArithmeticSlices(nums));
        System.out.println(numberOfArithmeticSlices2(nums));
    }

    /**
     * 执行结果：通过
     * 执行用时：0 ms, 在所有 Java 提交中击败了100.00%的用户
     * 内存消耗：36.2 MB, 在所有 Java 提交中击败了51.39%的用户
     */
    public int numberOfArithmeticSlices(int[] nums) {
        if (nums == null || nums.length <= 2) return 0;
        int res = 0, add = 0;
        for (int i = 2; i < nums.length; i++) {
            if (nums[i - 1] - nums[i] == nums[i - 2] - nums[i - 1]) res += ++add;
            else add = 0;
        }
        return res;
    }

    /**
     * 执行结果：通过
     * 执行用时：1 ms, 在所有 Java 提交中击败了8.26%的用户
     * 内存消耗：39.4 MB, 在所有 Java 提交中击败了7.80%的用户
     */
    public int numberOfArithmeticSlices2(int[] nums) {
        if (nums == null || nums.length <= 2) return 0;
        int[] dp = new int[nums.length];
        for (int i = 1; i < nums.length - 1; i++) {
            if (nums[i - 1] + nums[i + 1] == nums[i] * 2) dp[i] = dp[i - 1] + 1;
        }
        return Arrays.stream(dp).sum();
    }


}
