package com.android.algorithm.lesson.dp;

import org.junit.Test;

import java.util.Arrays;

public class knapsack_complete {

    @Test
    public void main() {
        int[] value = new int[]{15, 20, 30};
        int[] weight = new int[]{1, 3, 4};
        int w = 4;
        System.out.println(maxValueInKnapsack(value, weight, w));
    }

    public int maxValueInKnapsack(int[] value, int[] weight, int w) {
        if (value == null || weight == null || value.length == 0 || value.length != weight.length)
            return 0;
        int n = value.length;
        int[][] dp = new int[n][w + 1];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < w; j++) {
                
            }
        }
        print(dp);
        return dp[n - 1][w];
    }

    private int cnt = 0;

    private void print(int[][] dp) {
        for (int[] ints : dp) {
            if (cnt % 2 == 0) System.err.println(Arrays.toString(ints));
            else System.out.println(Arrays.toString(ints));
        }
        System.out.println();
        cnt++;
    }

}
