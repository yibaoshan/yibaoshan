package com.android.algorithm.leetcode;

import org.junit.Test;

public class Medium_64_最小路径和 {

    @Test
    public void main() {
        int[][] grid = new int[][]{
                new int[]{1, 3, 1},
                new int[]{1, 5, 1},
                new int[]{4, 2, 1},
        };
        System.out.println(minPathSum(grid));
    }

    /**
     * 简单dp，思路：
     * 1. 初始化第一行和第一列的值，他们的值是加上上一步
     * 2. 遍历当前矩阵，选左边和上面最小的一个值即可
     * 执行结果：通过
     * 执行用时：2 ms, 在所有 Java 提交中击败了95.54%的用户
     * 内存消耗：43.7 MB, 在所有 Java 提交中击败了33.06%的用户
     */
    public int minPathSum(int[][] grid) {
        if (grid == null || grid.length == 0) return 0;
        int m = grid.length, n = grid[0].length;
        for (int i = 1; i < m; i++) grid[i][0] += grid[i - 1][0];
        for (int j = 1; j < n; j++) grid[0][j] += grid[0][j - 1];
        for (int i = 1; i < m; i++) for (int j = 1; j < n; j++) grid[i][j] += Math.min(grid[i - 1][j], grid[i][j - 1]);
        return grid[m - 1][n - 1];
    }

}
