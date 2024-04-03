package com.android.algorithm.leetcode;

import org.junit.Test;

import java.util.Arrays;

public class Medium_825_适龄的朋友 {

    @Test
    public void main() {
        int[] ages = new int[]{16, 17, 18};
        System.out.println(numFriendRequests(ages));
    }

    public int numFriendRequests(int[] ages) {
        if (ages == null || ages.length == 0) return 0;
        Arrays.sort(ages);
        int left = 0, right = 0, res = 0;
        for (int age : ages) {
            if (age < 15) {
                continue;
            }
            while (ages[left] <= 0.5 * age + 7) {
                ++left;
            }
            while (right + 1 < ages.length && ages[right + 1] <= age) {
                ++right;
            }
            res += right - left;
        }
        return res;
    }

    private boolean sent(int x, int y) {
        if (y <= 0.5 * x + 7) return false;
        if (y > x) return false;
        if (y > 100 && x < 100) return false;
        return true;
    }

}
