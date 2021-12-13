package com.android.algorithm.leetcode;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class Medium_1091_二进制矩阵中的最短路径 {

    /**
     * 二进制矩阵中的 畅通路径 是一条从 左上角 单元格（即，(0, 0)）到 右下角 单元格（即，(n - 1, n - 1)）的路径，该路径同时满足下述要求：
     * <p>
     * 路径途经的所有单元格都的值都是 0 。
     * 路径中所有相邻的单元格应当在 8 个方向之一 上连通（即，相邻两单元之间彼此不同且共享一条边或者一个角）。
     * 畅通路径的长度 是该路径途经的单元格总数。
     * <p>
     * 来源：力扣（LeetCode）
     * 链接：https://leetcode-cn.com/problems/shortest-path-in-binary-matrix
     * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
     */

    @Test
    public void main() {

    }

    /**
     * 深度优先遍历，思路：
     * 1. 双重循环检查每个元素，遇到1则将周围能连起来的元素都变为2，防止重复遍历
     * <p>
     * 执行结果：通过
     * 执行用时：14 ms, 在所有 Java 提交中击败了60.30%的用户
     * 内存消耗：39.1 MB, 在所有 Java 提交中击败了97.68%的用户
     */
    public int shortestPathBinaryMatrix(int[][] grid) {
        int[][] temp = new int[][]{{1, 0}, {-1, 0}, {0, 1}, {0, -1}, {1, 1}, {1, -1}, {-1, 1}, {-1, -1}};
        int n = grid.length;
        if (grid[0][0] == 1 || grid[n - 1][n - 1] == 1) return -1;
        if (n == 1 && grid[0][0] == 0) return 1;
        Queue<List<int[]>> queue = new LinkedList<>();
        List<int[]> list = new ArrayList<>();
        list.add(new int[]{0, 0});
        queue.offer(list);
        grid[0][0] = 1;
        int ans = 1;
        while (!queue.isEmpty()) {
            List<int[]> curList = new ArrayList<>();
            list = new ArrayList<>(queue.poll());
            for (int[] arr : list) {
                for (int i = 0; i < 8; i++) {
                    int curI = arr[0] + temp[i][0];
                    int curJ = arr[1] + temp[i][1];
                    if (curI == n - 1 && curJ == n - 1) return ++ans;
                    if (0 <= curI && curI < n && 0 <= curJ && curJ < n && grid[curI][curJ] == 0) {
                        curList.add(new int[]{curI, curJ});
                        grid[curI][curJ] = 1;
                    }
                }
            }
            if (curList.size() > 0) {
                queue.add(curList);
                ans++;
            }
        }
        return -1;
    }

}
