package com.android.algorithm.leetcode;

import org.junit.Test;

public class Medium_221_最大正方形 {

    @Test
    public void main() {
//        char[][] matrix = new char[][]{
//                new char[]{'1', '0', '1', '0', '0'},
//                new char[]{'1', '0', '1', '1', '1'},
//                new char[]{'1', '1', '1', '1', '1'},
//                new char[]{'1', '0', '0', '1', '0'}
//        };
        char[][] matrix = new char[][]{
                new char[]{'1', '1', '1', '1', '0'},
                new char[]{'1', '1', '1', '1', '0'},
                new char[]{'1', '1', '1', '1', '1'},
                new char[]{'1', '1', '1', '1', '1'},
                new char[]{'0', '0', '1', '1', '1'},
        };
        System.out.println(maximalSquare(matrix));
        System.out.println(maximalSquare2(matrix));
    }

    /**
     * 思路，当以右下点判断是否是正方形时，左边、上面、左上肯定也是其他正方形的右下角
     */
    public int maximalSquare(char[][] matrix) {
        int max = 0;
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                if (matrix[i][j] == '0') continue;
                if (i > 0 && j > 0) {
                    int left = Character.getNumericValue(matrix[i][j - 1]);
                    int top = Character.getNumericValue(matrix[i - 1][j]);
                    int leftTop = Character.getNumericValue(matrix[i - 1][j - 1]);
                    matrix[i][j] = Character.forDigit(Math.min(Math.min(left, top), leftTop) + 1, 10);
                }
                max = Math.max(max, Character.getNumericValue(matrix[i][j]));
            }
        }
        return max > 1 ? max * max : max;
    }

    public int maximalSquare2(char[][] matrix) {
        int maxSide = 0;
        if (matrix == null || matrix.length == 0 || matrix[0].length == 0) {
            return maxSide;
        }
        int rows = matrix.length, columns = matrix[0].length;
        int[][] dp = new int[rows][columns];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                if (matrix[i][j] == '1') {
                    if (i == 0 || j == 0) {
                        dp[i][j] = 1;
                    } else {
                        dp[i][j] = Math.min(Math.min(dp[i - 1][j], dp[i][j - 1]), dp[i - 1][j - 1]) + 1;
                    }
                    maxSide = Math.max(maxSide, dp[i][j]);
                }
            }
        }
        return maxSide * maxSide;
    }

}
