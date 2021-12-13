package com.android.algorithm.leetcode;

import org.junit.Test;

public class Medium_200_岛屿数量 {

    /**
     * 给你一个由 '1'（陆地）和 '0'（水）组成的的二维网格，请你计算网格中岛屿的数量。
     * <p>
     * 岛屿总是被水包围，并且每座岛屿只能由水平方向和/或竖直方向上相邻的陆地连接形成。
     * <p>
     * 此外，你可以假设该网格的四条边均被水包围。
     * <p>
     * <p>
     * 来源：力扣（LeetCode）
     * 链接：https://leetcode-cn.com/problems/number-of-islands
     * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
     */

    @Test
    public void main(String[] args) {

    }

    /**
     * 深度优先遍历，思路：
     * 1. 双重循环检查每个元素，遇到1则将周围能连起来的元素都变为2，防止重复遍历
     *
     * 执行结果：通过
     * 执行用时：3 ms, 在所有 Java 提交中击败了82.15%的用户
     * 内存消耗：46.5 MB, 在所有 Java 提交中击败了82.75%的用户
     * */
    public int numIslands(char[][] grid) {
        int res = 0;
        for(int i = 0; i < grid.length; i++){
            for(int j = 0; j < grid[0].length; j++){
                if(grid[i][j] == '1'){
                    infect(grid, i, j);
                    res++;
                }
            }
        }
        return res;
    }
    //感染函数
    public void infect(char[][] grid, int i, int j){
        if(i < 0 || i >= grid.length ||
                j < 0 || j >= grid[0].length || grid[i][j] != '1'){
            return;
        }
        grid[i][j] = '2';
        infect(grid, i + 1, j);
        infect(grid, i - 1, j);
        infect(grid, i, j + 1);
        infect(grid, i, j - 1);
    }

}
