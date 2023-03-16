package com.android.algorithm.leetcode;

import org.junit.Test;

import java.util.Arrays;

public class Medium_322_零钱兑换 {

    /**
     * 给你一个整数数组 coins ，表示不同面额的硬币；以及一个整数 amount ，表示总金额。
     * <p>
     * 计算并返回可以凑成总金额所需的 最少的硬币个数 。如果没有任何一种硬币组合能组成总金额，返回 -1 。
     * <p>
     * 你可以认为每种硬币的数量是无限的。
     * <p>
     * 来源：力扣（LeetCode）
     * 链接：https://leetcode-cn.com/problems/coin-change
     * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
     */

    @Test
    public void main() {
        int[] coins = new int[]{1, 2};
        int amount = 2;
        System.out.println(coinChange(coins, amount));
    }

    /**
     * 动态规划
     * 执行结果：通过
     * 执行用时：11 ms, 在所有 Java 提交中击败了96.05%的用户
     * 内存消耗：38 MB, 在所有 Java 提交中击败了24.08%的用户
     */
    public int coinChange(int[] coins, int amount) {
        if (amount == 0) return 0;
        int max = Integer.MAX_VALUE;
        int[] dp = new int[amount + 1];
        Arrays.fill(dp, max);
        dp[0] = 0;
        for (int i = 0; i < coins.length; i++) {
            for (int j = coins[i]; j <= amount; j++) {
                if (dp[j - coins[i]] != max) {
                    dp[j] = Math.min(dp[j], dp[j - coins[i]] + 1);
                }
            }
        }
        return dp[amount] == max ? -1 : dp[amount];
    }

}
