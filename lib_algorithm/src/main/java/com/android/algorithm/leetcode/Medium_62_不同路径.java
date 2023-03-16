package com.android.algorithm.leetcode;

import org.junit.Test;

import java.util.Arrays;

public class Medium_62_不同路径 {

    /**
     * 一个机器人位于一个 m x n 网格的左上角 （起始点在下图中标记为 “Start” ）。
     * <p>
     * 机器人每次只能向下或者向右移动一步。机器人试图达到网格的右下角（在下图中标记为 “Finish” ）。
     * <p>
     * 问总共有多少条不同的路径？
     * <p>
     * 来源：力扣（LeetCode）
     * 链接：https://leetcode-cn.com/problems/unique-paths
     * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
     */

    @Test
    public void main() {
        int m = 3;
        int n = 7;
        System.out.println(uniquePaths(m, n));
        System.out.println(uniquePaths2(m, n));
        System.out.println(uniquePaths3(m, n));
    }

    /**
     * 动规五步法：
     * 1. 确定dp数组以及下标含义，dp[m][n]，i/j条路径
     * 2. 确定递推公式，dp[i][j]=dp[i-1][j]+dp[i][j-1]
     * 3. dp初始化，dp[i][0]=1，dp[0][i]=1
     * 4. 确定遍历顺序，从左到右
     * 5. 举例推导dp数组
     * <p>
     * 执行结果：通过
     * 执行用时：0 ms, 在所有 Java 提交中击败了100.00%的用户
     * 内存消耗：35.1 MB, 在所有 Java 提交中击败了64.72%的用户
     */
    public int uniquePaths(int m, int n) {
        int[][] dp = new int[m][n];
        for (int i = 0; i < m; i++) {
            dp[i][0] = 1;
        }
        for (int i = 0; i < n; i++) {
            dp[0][i] = 1;
        }
        for (int i = 1; i < m; i++) {
            for (int j = 1; j < n; j++) {
                dp[i][j] = dp[i - 1][j] + dp[i][j - 1];
            }
        }
        return dp[m - 1][n - 1];
    }

    private void print(int[][] dp) {
        for (int i = 0; i < dp.length; i++) {
            System.out.println(Arrays.toString(dp[i]));
        }
        System.out.println();
    }

    public int uniquePaths2(int m, int n) {
        int[][] matrix = new int[m][n];
        for (int i = 0; i < m; i++) {
            matrix[i][0] = 1;
        }
        Arrays.fill(matrix[0], 1);
        for (int i = 1; i < m; i++) {
            for (int j = 1; j < n; j++) {
                matrix[i][j] = matrix[i - 1][j] + matrix[i][j - 1];
            }
        }
        return matrix[m - 1][n - 1];
    }

    /**
     * 1
     * 2
     * 3
     * 4
     * 5
     * 6
     * 7
     * 7
     * 8
     * 9
     * 0
     * 0
     * 0
     * 00
     * <p>
     * 0
     */
    public int uniquePaths3(int m, int n) {
        int[][] dp = new int[m][n];
        Arrays.fill(dp[0], 1);
        for (int i = 0; i < m; i++) dp[i][0] = 1;
        for (int i = 1; i < m; i++) for (int j = 1; j < n; j++) dp[i][j] = dp[i - 1][j] + dp[i][j - 1];
        return dp[m - 1][n - 1];
    }

}
