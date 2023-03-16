package com.android.algorithm.leetcode;

import org.junit.Test;

public class Medium_931_下降路径最小和 {

    @Test
    public void main() {
        //[],[],[]
        int[][] matrix = new int[][]{
                new int[]{2, 1, 3},
                new int[]{6, 5, 4},
                new int[]{7, 8, 9},
        };
        System.out.println(minFallingPathSum(matrix));
    }

    /**
     * 原地DP
     * 执行结果：通过
     * 执行用时：5 ms, 在所有 Java 提交中击败了61.45%的用户
     * 内存消耗：42 MB, 在所有 Java 提交中击败了8.10%的用户
     */
    public int minFallingPathSum(int[][] matrix) {
        if (matrix == null || matrix.length == 0) return 0;
        LeetCodeUtil.print(matrix);
        System.out.println();
        int min = Integer.MAX_VALUE;
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                if (i > 0) {
                    int cur = matrix[i - 1][j];
                    if (j - 1 >= 0) cur = Math.min(cur, matrix[i - 1][j - 1]);
                    if (j + 1 < matrix[i].length) cur = Math.min(cur, matrix[i - 1][j + 1]);
                    matrix[i][j] += cur;
                }
                if (i == matrix.length - 1) min = Math.min(min, matrix[i][j]);
            }
        }
        LeetCodeUtil.print(matrix);
        return min;
    }

}
