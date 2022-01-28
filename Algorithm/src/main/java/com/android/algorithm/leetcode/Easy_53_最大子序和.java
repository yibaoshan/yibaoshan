package com.android.algorithm.leetcode;

import org.junit.Test;

import java.util.Arrays;

public class Easy_53_最大子序和 {

    /**
     * 给定一个整数数组 nums ，找到一个具有最大和的连续子数组（子数组最少包含一个元素），返回其最大和。
     * <p>
     *  
     * <p>
     * 示例 1：
     * <p>
     * 输入：nums = [-2,1,-3,4,-1,2,1,-5,4]
     * 输出：6
     * 解释：连续子数组 [4,-1,2,1] 的和最大，为 6 。
     * 示例 2：
     * <p>
     * 输入：nums = [1]
     * 输出：1
     * 示例 3：
     * <p>
     * 输入：nums = [0]
     * 输出：0
     * 示例 4：
     * <p>
     * 输入：nums = [-1]
     * 输出：-1
     * <p>
     * <p>
     * 来源：力扣（LeetCode）
     * 链接：https://leetcode-cn.com/problems/maximum-subarray
     * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
     */

    @Test
    public void main() {
//        int[] nums = new int[]{-2, 1};
        int[] nums = new int[]{5, 4, -1, 7, 8};
        System.out.println(maxSubArray(nums));
        System.out.println(maxSubArray2(nums));
    }

    /**
     * 执行结果：通过
     * 执行用时：1 ms, 在所有 Java 提交中击败了92.07%的用户
     * 内存消耗：48.4 MB, 在所有 Java 提交中击败了14.28%的用户
     * <p>
     * 解题思路：
     * sum 大于0 ，则代表下一个数有可以相加的必要性
     * 否则，让sum等于当前的数即可
     */
    public int maxSubArray(int[] nums) {
        int max = nums[1], sum = 0;
        for (int num : nums) {
            if (sum > 0) {
                sum += num;
            } else sum = num;
            max = Math.max(max, sum);
        }
        return max;
    }

    /**
     * 动态规划，当前i记录能到i位置的最大值
     * 执行结果：通过
     * 执行用时：22 ms, 在所有 Java 提交中击败了5.35%的用户
     * 内存消耗：51.6 MB, 在所有 Java 提交中击败了5.02%的用户
     * 否则，让sum等于当前的数即可
     */
    public int maxSubArray2(int[] nums) {
        if (nums == null || nums.length == 0) return 0;
        int[] dp = new int[nums.length];
        dp[0] = nums[0];
        System.err.println(Arrays.toString(nums));
        for (int i = 1; i < nums.length; i++) {
            dp[i] = Math.max(dp[i - 1] + nums[i], nums[i]);
            System.out.println(i + "=" + Arrays.toString(dp));
        }
        Arrays.sort(dp);
        return dp[dp.length - 1];
    }

}
