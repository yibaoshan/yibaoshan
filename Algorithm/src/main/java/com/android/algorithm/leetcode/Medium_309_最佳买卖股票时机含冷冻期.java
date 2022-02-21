package com.android.algorithm.leetcode;

import org.junit.Test;

import java.util.Arrays;

public class Medium_309_最佳买卖股票时机含冷冻期 {

    /**
     * 给定一个整数数组prices，其中第  prices[i] 表示第 i 天的股票价格 。​
     * <p>
     * 设计一个算法计算出最大利润。在满足以下约束条件下，你可以尽可能地完成更多的交易（多次买卖一支股票）:
     * <p>
     * 卖出股票后，你无法在第二天买入股票 (即冷冻期为 1 天)。
     * 注意：你不能同时参与多笔交易（你必须在再次购买前出售掉之前的股票）。
     * <p>
     * <p>
     * 来源：力扣（LeetCode）
     * 链接：https://leetcode-cn.com/problems/best-time-to-buy-and-sell-stock-with-cooldown
     * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
     */

    @Test
    public void main() {
        int[] prices = new int[]{1, 2, 3, 0, 2};
        System.out.println(maxProfit(prices));
    }

    /**
     * 动态规划，评论区答案
     * 执行结果：通过
     * 执行用时：1 ms, 在所有 Java 提交中击败了84.04%的用户
     * 内存消耗：39.8 MB, 在所有 Java 提交中击败了5.43%的用户
     */
    public int maxProfit(int[] prices) {
        int n = prices.length;
        // dp[i][0]: 表示第i天持有股票时累计最大收益。
        // dp[i][1]: 表示第i天出售了股票，第i天结束时不持有股票并且处于冷冻期时累计最大收益。
        // dp[i][2]: 表示第i天结束时不持有股票且第i+1天可以购买股票(不在冷冻期)时累计最大收益。
        int[][] dp = new int[n][3];
        dp[0][0] = -prices[0]; // 初始状态
        for (int i = 1; i < n; ++i) {
            dp[i][0] = Math.max(dp[i - 1][0], dp[i - 1][2] - prices[i]);
            dp[i][1] = dp[i - 1][0] + prices[i];
            dp[i][2] = Math.max(dp[i - 1][1], dp[i - 1][2]);
        }
        return Math.max(dp[n - 1][1], dp[n - 1][2]);
    }

}
