package com.android.algorithm.leetcode;

import org.junit.Test;

public class Medium_695_岛屿的最大面积 {

    /**
     * 给你一个大小为 m x n 的二进制矩阵 grid 。
     * <p>
     * 岛屿 是由一些相邻的 1 (代表土地) 构成的组合，这里的「相邻」要求两个 1 必须在 水平或者竖直的四个方向上 相邻。你可以假设 grid 的四个边缘都被 0（代表水）包围着。
     * <p>
     * 岛屿的面积是岛上值为 1 的单元格的数目。
     * <p>
     * 计算并返回 grid 中最大的岛屿面积。如果没有岛屿，则返回面积为 0 。
     * <p>
     *  
     * <p>
     * 来源：力扣（LeetCode）
     * 链接：https://leetcode-cn.com/problems/max-area-of-island
     * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
     */

    @Test
    public void main() {
        int[][] grid = new int[][]{new int[]{0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0}
                , new int[]{0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 0, 0, 0}
                , new int[]{0, 1, 1, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0}
                , new int[]{0, 1, 0, 0, 1, 1, 0, 0, 1, 0, 1, 0, 0}
                , new int[]{0, 1, 0, 0, 1, 1, 0, 0, 1, 1, 1, 0, 0}
                , new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0}
                , new int[]{0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 0, 0, 0}
                , new int[]{0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0}};
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                System.out.print(grid[i][j] + " ");
            }
            System.out.println();
        }
        System.out.println(maxAreaOfIsland(grid));
    }

    /**
     * 思路：深度遍历，从每个ij坐标开始，等于1则开始遍历当前坐标的上下左右节点，以此类推。
     * 执行结果：通过
     * 执行用时：2 ms, 在所有 Java 提交中击败了99.98%的用户
     * 内存消耗：38.9 MB, 在所有 Java 提交中击败了57.87%的用户
     */
    public int maxAreaOfIsland(int[][] grid) {
        int res = 0;
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                if (grid[i][j] != 1) continue;
                res = Math.max(res, dfs(grid, i, j, grid.length, grid[0].length));
            }
        }
        return res;
    }

    public int dfs(int[][] grid, int i, int j, int m, int n) {
        if (i < 0 || j < 0 || i >= m || j >= n || grid[i][j] == 0) return 0;
        int res = 1;
        grid[i][j] = 0;
        res += dfs(grid, i - 1, j, m, n);
        res += dfs(grid, i + 1, j, m, n);
        res += dfs(grid, i, j - 1, m, n);
        res += dfs(grid, i, j + 1, m, n);
        return res;
    }

}
