package com.android.algorithm.leetcode;

import org.junit.Test;

public class Easy_746_使用最小花费爬楼梯 {

    /**
     * 给你一个整数数组 cost ，其中 cost[i] 是从楼梯第 i 个台阶向上爬需要支付的费用。一旦你支付此费用，即可选择向上爬一个或者两个台阶。
     * <p>
     * 你可以选择从下标为 0 或下标为 1 的台阶开始爬楼梯。
     * <p>
     * 请你计算并返回达到楼梯顶部的最低花费。
     * <p>
     * 来源：力扣（LeetCode）
     * 链接：https://leetcode-cn.com/problems/min-cost-climbing-stairs
     * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
     */

    @Test
    public void main() {
        int[] cost = new int[]{10, 15, 20};
        System.out.println(minCostClimbingStairs(cost));
    }

    /**
     * 动态规划
     * 执行结果：通过
     * 执行用时：1 ms, 在所有 Java 提交中击败了82.71%的用户
     * 内存消耗：38 MB, 在所有 Java 提交中击败了80.42%的用户
     */
    public int minCostClimbingStairs(int[] cost) {
        int[] dp = new int[cost.length];
        dp[0] = cost[0];
        dp[1] = cost[1];
        for (int i = 2; i < cost.length; i++) {
            dp[i] = Math.min(dp[i - 1], dp[i - 2]) + cost[i];
        }
        return Math.min(dp[dp.length - 1], dp[dp.length - 2]);
    }

}
