package com.android.algorithm.leetcode;

import org.junit.Test;

import java.util.Arrays;

public class Medium_740_删除并获得点数 {

    /**
     * 给你一个整数数组 nums ，你可以对它进行一些操作。
     * <p>
     * 每次操作中，选择任意一个 nums[i] ，删除它并获得 nums[i] 的点数。之后，你必须删除 所有 等于 nums[i] - 1 和 nums[i] + 1 的元素。
     * <p>
     * 开始你拥有 0 个点数。返回你能通过这些操作获得的最大点数。
     * <p>
     * 来源：力扣（LeetCode）
     * 链接：https://leetcode-cn.com/problems/delete-and-earn
     * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
     */

    @Test
    public void main() {
//        int[] nums = new int[]{3, 4, 2};
        int[] nums = new int[]{3, 1};
//        int[] nums = new int[]{2, 2, 3, 3, 3, 4};
        System.out.println(deleteAndEarn(nums));
    }

    /**
     * 动态规划
     * 执行结果：通过
     * 执行用时：2 ms, 在所有 Java 提交中击败了61.64%的用户
     * 内存消耗：41.1 MB, 在所有 Java 提交中击败了5.07%的用户
     */
    public int deleteAndEarn(int[] nums) {
        if (nums == null || nums.length == 0) return 0;
        if (nums.length == 1) return nums[0];
        Arrays.sort(nums);
        int[] array = new int[nums[nums.length - 1] + 1];
        for (int i = 0; i < nums.length; i++) {
            array[nums[i]]++;
        }
        int[] dp = new int[array.length];
        dp[1] = array[1];
        for (int i = 2; i < array.length; i++) {
            dp[i] = Math.max(array[i] * i + dp[i - 2], dp[i - 1]);
            System.out.println(Arrays.toString(dp));
        }
        return dp[dp.length - 1];
    }

}
