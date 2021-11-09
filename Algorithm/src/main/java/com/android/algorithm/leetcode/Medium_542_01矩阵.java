package com.android.algorithm.leetcode;

import java.util.LinkedList;
import java.util.Queue;

public class Medium_542_01矩阵 {

    /**
     * 给定一个由 0 和 1 组成的矩阵 mat ，请输出一个大小相同的矩阵，其中每一个格子是 mat 中对应位置元素到最近的 0 的距离。
     * <p>
     * 两个相邻元素间的距离为 1 。
     * <p>
     * 来源：力扣（LeetCode）
     * 链接：https://leetcode-cn.com/problems/01-matrix
     * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
     */

    public static void main(String[] args) {
        int[][] mat = new int[][]{new int[]{0, 0, 0}, new int[]{0, 1, 0}, new int[]{1, 1, 1}};
        for (int i = 0; i < mat.length; i++) {
            for (int j = 0; j < mat[i].length; j++) {
                System.out.print(mat[i][j] + " ");
            }
            System.out.println();
        }
        System.out.println();
        int[][] result = updateMatrix2(mat);
        for (int i = 0; i < result.length; i++) {
            for (int j = 0; j < result[i].length; j++) {
                System.out.print(result[i][j] + " ");
            }
            System.out.println();
        }
    }

    /**
     * 暴力解法：矩阵中所有为0的下标值都保存到list
     * 再遍历矩阵，不为0则遍历list，找到离得最近的下标
     * 执行结果：通过
     * 执行用时：2038 ms, 在所有 Java 提交中击败了5.04%的用户
     * 内存消耗：40.5 MB, 在所有 Java 提交中击败了86.14%的用户
     */
    public static int[][] updateMatrix(int[][] mat) {
        LinkedList<int[]> list = new LinkedList<>();
        for (int i = 0; i < mat.length; i++) {
            for (int j = 0; j < mat[i].length; j++) {
                if (mat[i][j] == 0) list.add(new int[]{i, j});
            }
        }
        int[][] result = mat.clone();
        for (int i = 0; i < result.length; i++) {
            for (int j = 0; j < result[i].length; j++) {
                int value = 0;
                if (result[i][j] != 0) {
                    value = Integer.MAX_VALUE;
                    for (int[] array : list) {
                        int distance = Math.abs(array[0] - i) + Math.abs(array[1] - j);
                        if (distance == 1) {
                            value = distance;
                            break;
                        }
                        value = Math.min(value, distance);
                    }
                }
                result[i][j] = value;
            }
        }
        return result;
    }

    /**
     * 评论区答案
     * */
    public static int[][] updateMatrix2(int[][] matrix) {
        int[] dx = new int[] {-1, 1, 0, 0};
        int[] dy = new int[] {0, 0, -1, 1};
        Queue<int[]> queue = new LinkedList<>();
        int m = matrix.length, n = matrix[0].length;
        int[][] res = new int[m][n];
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (matrix[i][j] == 0) {
                    for (int k = 0; k < 4; k++) {
                        int x = i + dx[k];
                        int y = j + dy[k];
                        if (x >= 0 && x < m && y >= 0 && y < n
                                && matrix[x][y] == 1 && res[x][y] == 0) {
                            // 这是在 0 边上的1。需要加上 res[x][y] == 0 的判断防止重复入队
                            res[x][y] = 1;
                            queue.offer(new int[] {x, y});
                        }
                    }
                }
            }
        }

        while (!queue.isEmpty()) {
            int[] point = queue.poll();
            int x = point[0], y = point[1];
            for (int i = 0; i < 4; i++) {
                int newX = x + dx[i];
                int newY = y + dy[i];
                if (newX >= 0 && newX < m && newY >= 0 && newY < n
                        && matrix[newX][newY] == 1 && res[newX][newY] == 0) {
                    res[newX][newY] = res[x][y] + 1;
                    queue.offer(new int[] {newX, newY});
                }
            }
        }

        return res;
    }

}
