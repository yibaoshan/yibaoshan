package com.android.algorithm.leetcode;

import org.junit.Test;

import java.util.Arrays;

public class Medium_64_最小路径和 {

    @Test
    public void main() {
//        int[][] grid = new int[][]{
//                new int[]{1, 3, 1},
//                new int[]{1, 5, 1},
//                new int[]{4, 2, 1},
//        };
        int[][] grid = new int[][]{
                new int[]{1, 2, 3},
                new int[]{4, 5, 6},
        };
        /**
         *  1 2 3
         *  4 5 6
         * */
        System.out.println(minPathSum(grid));
    }

    /**
     * 动规，申请一个 m x n 的数组
     *
     * 数组每个元素保存的是，走到该元素时，使用的最小步数
     *
     * 初始化 m 行和 n 行即可
     * */
    public int minPathSum(int[][] grid) {
        if (grid == null || grid.length < 1) return 0;
        int m = grid.length, n = grid[0].length;
        int[][] dp = new int[m][n];
        int total = 0;
        for (int i = 0; i < m; i++) {
            total += grid[i][0];
            dp[i][0] = total;
        }
        total = 0;
        for (int i = 0; i < n; i++) {
            total += grid[0][i];
            dp[0][i] = total;
        }
        for (int i = 1; i < grid.length; i++) {
            for (int j = 1; j < grid[i].length; j++) {
                dp[i][j] = Math.min(dp[i][j - 1], dp[i - 1][j]) + grid[i][j];
            }
        }
        return dp[m - 1][n - 1];
    }

    private void print(int[][] grid) {
        for (int i = 0; i < grid.length; i++) {
            System.out.println(Arrays.toString(grid[i]));
        }
    }

}
