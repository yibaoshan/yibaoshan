package com.android.algorithm.leetcode;

import org.junit.Test;

import java.util.LinkedList;
import java.util.Queue;

public class Easy_566_重塑矩阵 {

    /**
     * 在 MATLAB 中，有一个非常有用的函数 reshape ，它可以将一个 m x n 矩阵重塑为另一个大小不同（r x c）的新矩阵，但保留其原始数据。
     * <p>
     * 给你一个由二维数组 mat 表示的 m x n 矩阵，以及两个正整数 r 和 c ，分别表示想要的重构的矩阵的行数和列数。
     * <p>
     * 重构后的矩阵需要将原始矩阵的所有元素以相同的 行遍历顺序 填充。
     * <p>
     * 如果具有给定参数的 reshape 操作是可行且合理的，则输出新的重塑矩阵；否则，输出原始矩阵。
     * <p>
     * 来源：力扣（LeetCode）
     * 链接：https://leetcode-cn.com/problems/reshape-the-matrix
     * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
     */

    @Test
    public void main() {
        int[][] mat = new int[][]{new int[]{1, 2}, new int[]{3, 4}};
        int r = 2;
        int c = 4;
        int[][] result = matrixReshape(mat, r, c);
        for (int i = 0; i < result.length; i++) {
            for (int j = 0; j < result[i].length; j++) {
                System.out.print(result[i][j] + "");
            }
            System.out.println();
        }
    }

    /**
     * 执行结果：通过
     * 执行用时：4 ms, 在所有 Java 提交中击败了6.08%的用户
     * 内存消耗：38.7 MB, 在所有 Java 提交中击败了30.94%的用户
     */
    public int[][] matrixReshape(int[][] mat, int r, int c) {
        Queue<Integer> queue = new LinkedList<>();
        for (int i = 0; i < mat.length; i++) {
            for (int j = 0; j < mat[i].length; j++) {
                queue.offer(mat[i][j]);
            }
        }
        int count = queue.size();
        if (count != r * c) return mat;
        int[][] result = new int[r][c];
        for (int i = 0; i < result.length; i++) {
            for (int j = 0; j < result[i].length; j++) {
                if (!queue.isEmpty()) result[i][j] = queue.poll();
            }
        }
        return result;
    }

    public int[][] matrixReshape2(int[][] mat, int r, int c) {
        int m = mat.length;
        int n = mat[0].length;
        if (m * n != r * c) {
            return mat;
        }
        int[][] result = new int[r][c];
        for (int x = 0; x < m * n; ++x) {
            result[x / c][x % c] = mat[x / n][x % n];
        }
        return result;
    }

}
