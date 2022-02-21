package com.android.algorithm.leetcode;

import org.junit.Test;

import java.util.Arrays;

public class Medium_714_买卖股票的最佳时机含手续费 {

    @Test
    public void main() {
//        int[] prices = new int[]{1, 3, 2, 8, 4, 9};
        int[] prices = new int[]{1, 3, 7, 5, 10, 3};
        int fee = 3;
        System.out.println(maxProfit(prices, fee));
    }

    /**
     * 动态规划，评论区答案
     * 执行结果：通过
     * 执行用时：19 ms, 在所有 Java 提交中击败了15.09%的用户
     * 内存消耗：49 MB, 在所有 Java 提交中击败了14.93%的用户
     */
    /**
     * 作者：sweetiee
     * 链接：https://leetcode-cn.com/problems/best-time-to-buy-and-sell-stock-with-transaction-fee/solution/jian-dan-dpmiao-dong-gu-piao-mai-mai-by-tejdo/
     * 来源：力扣（LeetCode）
     * 著作权归作者所有。商业转载请联系作者获得授权，非商业转载请注明出处。
     */
    public int maxProfit(int[] prices, int fee) {
        int n = prices.length;
        int[][] dp = new int[n][2];
        dp[0][0] = 0;
        dp[0][1] = -prices[0];
        for (int i = 1; i < n; i++) {
            dp[i][0] = Math.max(dp[i - 1][0], dp[i - 1][1] + prices[i] - fee);
            dp[i][1] = Math.max(dp[i - 1][1], dp[i - 1][0] - prices[i]);
        }
        return dp[n - 1][0];
    }

}
