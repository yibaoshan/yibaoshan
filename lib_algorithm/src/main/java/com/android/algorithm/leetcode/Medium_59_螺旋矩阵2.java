package com.android.algorithm.leetcode;

import org.junit.Test;

import java.util.Arrays;

public class Medium_59_螺旋矩阵2 {

    @Test
    public void main() throws InterruptedException {
        int[][] res = generateMatrix(3);
        for (int[] ints : res) System.out.println(Arrays.toString(ints));
    }

    /**
     * 评论区答案
     * 执行结果：通过
     * 执行用时：0 ms, 在所有 Java 提交中击败了100.00%的用户
     * 内存消耗：36.2 MB, 在所有 Java 提交中击败了88.53%的用户
     */
    public int[][] generateMatrix(int n) throws InterruptedException {
        int[][] res = new int[n][n];
        int total = (int) Math.pow(n, 2);
        int count = 1, row = 0;
        while (count <= total) {
            for (int i = row; i < n - row; i++) {
                res[row][i] = count++;
            }
            for (int i = row + 1; i < n - row; i++) {
                res[i][n - row - 1] = count++;
            }
            for (int i = n - row - 2; i >= row; i--) {
                res[n - row - 1][i] = count++;
            }
            for (int i = n - row - 2; i > row; i--) {
                res[i][row] = count++;
            }
            row++;
        }
        return res;
    }


}
