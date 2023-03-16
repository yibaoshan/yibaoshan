package com.android.algorithm.leetcode;

import org.junit.Test;

public class Medium_96_不同的二叉搜索树 {

    @Test
    public void main() {
        System.out.println(numTrees(3));
    }

    /**
     * 动态规划：
     * 1. dp[i] = i棵树时的
     * 随想录答案，看不懂
     * 执行结果：通过
     * 执行用时：0 ms, 在所有 Java 提交中击败了100.00%的用户
     * 内存消耗：35.1 MB, 在所有 Java 提交中击败了52.85%的用户
     */
    public int numTrees(int n) {
        if (n < 3) return n;
        //初始化 dp 数组
        int[] dp = new int[n + 1];
        //初始化0个节点和1个节点的情况
        dp[0] = 1;
        dp[1] = 1;
        for (int i = 2; i <= n; i++) {
            for (int j = 1; j <= i; j++) {
                //对于第i个节点，需要考虑1作为根节点直到i作为根节点的情况，所以需要累加
                //一共i个节点，对于根节点j时,左子树的节点个数为j-1，右子树的节点个数为i-j
                dp[i] += dp[j - 1] * dp[i - j];
            }
        }
        return dp[n];
    }

}
