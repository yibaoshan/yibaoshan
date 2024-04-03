package com.android.algorithm.leetcode;

import org.junit.Test;

public class Medium_2029_石子游戏IX {

    @Test
    public void main() {
        int[] stones = new int[]{5, 1, 2, 4, 3};
        System.out.println(stoneGameIX(stones));
    }

    /**
     * 直觉告诉我可以用动态规划，但是我不会，哈哈哈
     * update：看了答案才知道是博弈类题型，又涨姿势了
     * 不会做，评论区答案
     * 执行结果：通过
     * 执行用时：4 ms, 在所有 Java 提交中击败了47.71%的用户
     * 内存消耗：49.9 MB, 在所有 Java 提交中击败了33.33%的用户
     */
    public boolean stoneGameIX(int[] stones) {
        int[] mod = new int[3];
        for (int stone : stones) {
            mod[stone % 3]++;
        }
        if (mod[0] % 2 == 0) {
            return mod[1] > 0 && mod[2] > 0;
        } else {
            return Math.abs(mod[1] - mod[2]) > 2;
        }
    }

}
