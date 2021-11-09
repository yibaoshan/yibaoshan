package com.android.algorithm.leetcode;

import org.junit.Test;

import java.util.LinkedList;
import java.util.Queue;

public class Medium_994_腐烂的橘子 {

    /**
     * 在给定的网格中，每个单元格可以有以下三个值之一：
     * <p>
     * 值 0 代表空单元格；
     * 值 1 代表新鲜橘子；
     * 值 2 代表腐烂的橘子。
     * 每分钟，任何与腐烂的橘子（在 4 个正方向上）相邻的新鲜橘子都会腐烂。
     * <p>
     * 返回直到单元格中没有新鲜橘子为止所必须经过的最小分钟数。如果不可能，返回 -1。
     * <p>
     * 来源：力扣（LeetCode）
     * 链接：https://leetcode-cn.com/problems/rotting-oranges
     * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
     */

    @Test
    public void main() {
        int[][] grid = new int[][]{
                new int[]{2, 0, 1, 1, 1, 1, 1, 1, 1, 1}
                , new int[]{1, 0, 1, 0, 0, 0, 0, 0, 0, 1}
                , new int[]{1, 0, 1, 0, 1, 1, 1, 1, 0, 1}
                , new int[]{1, 0, 1, 0, 1, 0, 0, 1, 0, 1}
                , new int[]{1, 0, 1, 0, 1, 0, 0, 1, 0, 1}
                , new int[]{1, 0, 1, 0, 1, 1, 0, 1, 0, 1}
                , new int[]{1, 0, 1, 0, 0, 0, 0, 1, 0, 1}
                , new int[]{1, 0, 1, 1, 1, 1, 1, 1, 0, 1}
                , new int[]{1, 0, 0, 0, 0, 0, 0, 0, 0, 1}
                , new int[]{1, 1, 1, 1, 1, 1, 1, 1, 1, 1}
        };
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                System.out.print(grid[i][j] + " ");
            }
            System.out.println();
        }
        System.out.println(orangesRotting(grid));
        System.out.println("-----------------------");
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                System.out.print(grid[i][j] + " ");
            }
            System.out.println();
        }
    }

    /**
     * 评论区答案
     * 执行结果：通过
     * 执行用时：2 ms, 在所有 Java 提交中击败了73.23%的用户
     * 内存消耗：37.6 MB, 在所有 Java 提交中击败了86.19%的用户
     */
    public int orangesRotting(int[][] grid) {
        int row = grid.length, rol = grid[0].length;
        int[][] dirs = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
        boolean[][] state = new boolean[row][rol];
        Queue<int[]> queue = new LinkedList<>();
        int count = 0;
        for (int i = 0; i < row; i++)
            for (int j = 0; j < rol; j++) {
                if (grid[i][j] == 0) count++;
                if (grid[i][j] == 2) {
                    state[i][j] = true;
                    queue.offer(new int[]{i, j});
                }
            }
        if (count == rol * row) return 0;
        while (!queue.isEmpty()) {
            int[] cur = queue.poll();
            int curRow = cur[0], curRol = cur[1];
            for (int i = 0; i < 4; i++) {
                int curI = curRow + dirs[i][0];
                int curJ = curRol + dirs[i][1];
                if (curI >= 0 && curI < row && curJ >= 0 && curJ < rol && !state[curI][curJ] && grid[curI][curJ] == 1) {
                    boolean falg = queue.offer(new int[]{curI, curJ});
                    state[curI][curJ] = true;
                    grid[curI][curJ] = grid[curRow][curRol] + 1;
                }
            }
        }
        int result = 0;
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < rol; j++) {
                if (grid[i][j] == 1) return -1;
                result = Math.max(result, grid[i][j]);
            }
        }
        return result - 2;
    }

}
