package com.android.algorithm.leetcode;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class Hard_149_直线上最多的点数 {

    /**
     * 给你一个数组 points ，其中 points[i] = [xi, yi] 表示 X-Y 平面上的一个点。求最多有多少个点在同一条直线上。
     */

    @Test
    public void main() {

    }

    /**
     * 评论区答案
     * 执行结果：通过
     * 执行用时：22 ms, 在所有 Java 提交中击败了18.23%的用户
     * 内存消耗：39.2 MB, 在所有 Java 提交中击败了5.11%的用户
     */
    public int maxPoints(int[][] points) {
        int n = points.length;
        if (n == 0) return 0;
        if (n == 1) return 1;
        int res = 0;
        for (int i = 0; i < n - 1; i++) {
            Map<String, Integer> map = new HashMap<>();
            int repeat = 0;
            int tmp_max = 0;
            for (int j = i + 1; j < n; j++) {
                int dy = points[i][1] - points[j][1];
                int dx = points[i][0] - points[j][0];
                if (dy == 0 && dx == 0) {
                    repeat++;
                    continue;
                }
                int g = gcd(dy, dx);
                if (g != 0) {
                    dy /= g;
                    dx /= g;
                }
                String tmp = String.valueOf(dy) + "/" + String.valueOf(dx);
                map.put(tmp, map.getOrDefault(tmp, 0) + 1);
                tmp_max = Math.max(tmp_max, map.get(tmp));
            }
            res = Math.max(res, repeat + tmp_max + 1);
        }
        return res;
    }

    private int gcd(int dy, int dx) {
        if (dx == 0) return dy;
        else return gcd(dx, dy % dx);
    }

}
