package com.android.algorithm.leetcode;

import org.junit.Test;

import java.util.Arrays;

public class Medium_204_求质数 {

    /**
     * 给定整数 n ，返回 所有小于非负整数 n 的质数的数量 。
     */

    @Test
    public void main() {
        System.out.println(countPrimes(10));
    }

    public int countPrimes(int n) {
        if (n < 2) return 0;
        int cnt = 0;
        boolean[] isPrimes = new boolean[n];
        Arrays.fill(isPrimes, true);
        isPrimes[0] = false;
        isPrimes[1] = false;
        for (int i = 2; i < Math.sqrt(n); i++) {
            if (!isPrimes[i]) continue;
            for (int j = i * i; j < n; j += i) {
                isPrimes[j] = false;
            }
        }
        for (boolean isPrime : isPrimes) {
            if (isPrime) cnt++;
        }
        return cnt;
    }

}
