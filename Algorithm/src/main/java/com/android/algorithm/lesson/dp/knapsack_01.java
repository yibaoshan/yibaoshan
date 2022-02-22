package com.android.algorithm.lesson.dp;

import org.junit.Test;

import java.util.Arrays;

public class knapsack_01 {

    @Test
    public void main() {
        int[] value = new int[]{15, 20, 30};
        int[] weight = new int[]{1, 3, 4};
        int w = 4;
        System.out.println(maxValueInKnapsack(value, weight, w));
    }

    /**
     * 一、dp[i][j]的定义
     * 能够承载重量为j的背包，在n件物品中，选择一个价值最大的物品，选择范围在0~i之间
     * <p>
     * 二、递推公式
     * 对于每个dp[i][j]而言，都有两个状态
     * 1. 当前物品i的重量太重了，放不进重量为j的背包，那么的dp[i][j]值只能取这个重量下的最大价值的物品，也就是dp[i-][j]
     * 2. 当前物品i的重量放的进重量为j的背包，那么dp[i][j]=value[i]，但是，放进去之后背包容量还有剩余，不能浪费，于是
     * 当前重量为j的背包-物品i的重量，剩余重量能够装下的最大价值也要加上来，那么当前的最大价值为：
     * 能放下物品i本身的价值value[i] + 剩余重量(j-weight[i])的最大价值dp[i-1][j-weight[i]]
     * <p>
     * 三、初始化dp数组
     * 由于dp[i][j]需要根据上一个最大价值来判定，所以我们可以将物品value[0](即i=0)放到每个重量为j的背包当中，即：
     * dp[0][j] = value[0]
     */
    public int maxValueInKnapsack(int[] value, int[] weight, int w) {
        if (value == null || weight == null || value.length == 0 || value.length != weight.length)
            return 0;
        int n = value.length;
        int[][] dp = new int[n][w + 1];
        for (int j = weight[0]; j <= w; j++) {
            dp[0][j] = value[0];
        }
        for (int i = 1; i < n; i++) {
            for (int j = 0; j <= w; j++) {
                if (weight[i] < j) {
                    dp[i][j] = Math.max(dp[i - 1][j], value[i] + dp[i - 1][j - weight[i]]);
                } else dp[i][j] = dp[i - 1][j];
                print(dp);
            }
        }
        return dp[n - 1][w];
    }

    private int cnt = 0;

    private void print(int[][] dp) {
        for (int[] ints : dp) {
            if (cnt % 2 == 0) System.err.println(Arrays.toString(ints));
            else System.out.println(Arrays.toString(ints));
        }
        System.out.println();
        cnt++;
    }

}
