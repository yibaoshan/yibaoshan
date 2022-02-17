package com.android.algorithm.leetcode;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Easy_1380_矩阵中的幸运数 {

    @Test
    public void main() {
        int[][] matrix = new int[][]{
                new int[]{3, 7, 8},
                new int[]{9, 11, 13},
                new int[]{15, 16, 17}
        };
        System.out.println(luckyNumbers(matrix));
    }

    public List<Integer> luckyNumbers(int[][] matrix) {
        List<Integer> list = new ArrayList<>();
        if (matrix == null || matrix.length == 0) return list;
        int[] minArray = new int[matrix.length];
        int[] maxArray = new int[matrix[0].length];
        Arrays.fill(minArray, Integer.MAX_VALUE);
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                minArray[i] = Math.min(minArray[i], matrix[i][j]);
                maxArray[j] = Math.max(maxArray[j], matrix[i][j]);
            }
        }
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                if (matrix[i][j] == minArray[i] && matrix[i][j] == maxArray[j]) list.add(matrix[i][j]);
            }
        }
        return list;
    }

}
