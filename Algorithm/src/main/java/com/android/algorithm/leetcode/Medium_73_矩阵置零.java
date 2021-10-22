package com.android.algorithm.leetcode;

import org.junit.Test;

import java.util.LinkedList;
import java.util.List;

public class Medium_73_矩阵置零 {

    @Test
    public void main() {
        int[][] matrix = new int[][]{new int[]{0, 1}};
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                System.out.print(matrix[i][j] + " ");
            }
            System.out.println();
        }
        setZeroes(matrix);
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                System.out.print(matrix[i][j] + " ");
            }
            System.out.println();
        }
    }

    /**
     * 执行结果：通过
     * 执行用时：2 ms, 在所有 Java 提交中击败了21.75%的用户
     * 内存消耗：39.8 MB, 在所有 Java 提交中击败了57.99%的用户
     */
    public void setZeroes(int[][] matrix) {
        int n = matrix[0].length;
        List<int[]> list = new LinkedList<>();
        for (int i = 0; i < matrix.length; i++) {
            if (matrix[i].length != n) break;
            for (int j = 0; j < matrix[i].length; j++) {
                if (matrix[i][j] == 0) list.add(new int[]{i, j});
            }
        }
        for (int i = 0; i < list.size(); i++) {
            set(matrix, list.get(i)[0], list.get(i)[1]);
        }
    }

    private void set(int[][] matrix, int m, int n) {
        for (int i = 0; i < matrix.length; i++) {
            matrix[i][n] = 0;
            if (m==i){
                for (int j = 0; j < matrix[i].length; j++) {
                    matrix[m][j] = 0;
                }
            }

        }
    }

}
