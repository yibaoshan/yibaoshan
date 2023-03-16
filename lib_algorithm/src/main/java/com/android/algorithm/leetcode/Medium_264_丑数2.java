package com.android.algorithm.leetcode;

import org.junit.Test;

public class Medium_264_丑数2 {

    /**
     * 给你一个整数 n ，请你找出并返回第 n 个 丑数 。
     * <p>
     * 丑数 就是只包含质因数 2、3 和/或 5 的正整数。
     */

    @Test
    public void main() {
        int n = 10;
        System.err.println("1, 2, 3, 4, 5, 6, 8, 9, 10, 12, 15, 16, 18, 20, 24, 25, 27, 30, 32, 36");
        System.out.println(nthUglyNumber(n));
    }

    /**
     * 动态规划，评论区答案，思路：
     * dp[i]为第i个丑数值
     * 初始dp[0]=1
     * 创建2/3/5三个指针，开始遍历数组，每次dp[i]为2/3/5乘上他们本身的次数中最小的值
     * <p>
     * 执行结果：通过
     * 执行用时：2 ms, 在所有 Java 提交中击败了99.46%的用户
     * 内存消耗：40.7 MB, 在所有 Java 提交中击败了10.00%的用户
     */
    public int nthUglyNumber(int n) {
        if (n < 1) return 0;
        int n2 = 0, n3 = 0, n5 = 0;
        int[] dp = new int[n];
        dp[0] = 1;
        for (int i = 1; i < n; i++) {
            dp[i] = Math.min(2 * dp[n2], Math.min(3 * dp[n3], 5 * dp[n5]));
            if (dp[i] == 2 * dp[n2]) n2++;
            if (dp[i] == 3 * dp[n3]) n3++;
            if (dp[i] == 5 * dp[n5]) n5++;
        }
        return dp[n - 1];
    }

}
