package com.android.algorithm.leetcode;

import org.junit.Test;

import java.util.Arrays;
import java.util.HashSet;

public class Medium_688_骑士在棋盘上的概率 {

    /**
     * 在一个 n x n 的国际象棋棋盘上，一个骑士从单元格 (row, column) 开始，并尝试进行 k 次移动。行和列是 从 0 开始 的，所以左上单元格是 (0,0) ，右下单元格是 (n - 1, n - 1) 。
     * <p>
     * 象棋骑士有8种可能的走法，如下图所示。每次移动在基本方向上是两个单元格，然后在正交方向上是一个单元格。
     * <p>
     * <p>
     * 来源：力扣（LeetCode）
     * 链接：https://leetcode-cn.com/problems/knight-probability-in-chessboard
     * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
     */

    @Test
    public void main() {
        int n = 3, k = 2, row = 0, column = 0;
        System.out.println(knightProbability(n, k, row, column));
    }

    int[][] dirs = new int[][]{{-1, -2}, {-1, 2}, {1, -2}, {1, 2}, {-2, 1}, {-2, -1}, {2, 1}, {2, -1}};

    /**
     * 动态规划，评论区答案
     * 执行结果：通过
     * 执行用时：8 ms, 在所有 Java 提交中击败了28.48%的用户
     * 内存消耗：41.1 MB, 在所有 Java 提交中击败了5.18%的用户
     */
    public double knightProbability(int n, int k, int row, int column) {
        double[][][] dp = new double[n][n][k + 1];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                dp[i][j][0] = 1;
            }
        }
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                System.out.print(Arrays.toString(dp[i][j]));
            }
            System.out.println("\n");
        }
        for (int p = 1; p <= k; p++) {
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    for (int[] d : dirs) {
                        int nx = i + d[0], ny = j + d[1];
                        if (nx < 0 || nx >= n || ny < 0 || ny >= n) continue;
                        dp[i][j][p] += dp[nx][ny][p - 1] / 8;
                    }
                }
            }
        }
        return dp[row][column][k];
    }

    public double knightProbability2(int n, int k, int row, int column) {
        backtrack(n, k, row, column);
        int[][] matrix = new int[n][n];
        for (int i = 0; i < n; i++) {
            System.out.println(Arrays.toString(matrix[i]));
        }
        return inside / (outside + inside);
    }

    private double inside = 0;
    private double outside = 0;

    private HashSet<String> hashSet = new HashSet<>();

    private void backtrack(int n, int k, int row, int column) {
        if (hashSet.contains(row + "" + column)) return;
        hashSet.add(row + "" + column);
        if (row < 0 || row >= n || column < 0 || column >= n) {
            outside++;
            return;
        }
        if (k <= 0) {
            inside++;
            System.err.println(row + "," + column);
            return;
        }
        backtrack(n, k - 1, row - 2, column - 1);
        backtrack(n, k - 1, row - 2, column + 1);
        backtrack(n, k - 1, row - 1, column - 2);
        backtrack(n, k - 1, row - 1, column + 2);

        backtrack(n, k - 1, row + 1, column - 2);
        backtrack(n, k - 1, row + 1, column + 2);
        backtrack(n, k - 1, row + 2, column - 1);
        backtrack(n, k - 1, row + 2, column + 1);
    }

}
