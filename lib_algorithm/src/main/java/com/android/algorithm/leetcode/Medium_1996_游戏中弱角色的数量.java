package com.android.algorithm.leetcode;

import org.junit.Test;

import java.util.Arrays;
import java.util.Comparator;

public class Medium_1996_游戏中弱角色的数量 {

    @Test
    public void main() {
        int[][] ints = new int[][]{
                new int[]{7, 11},
                new int[]{1, 2},
                new int[]{9, 7},
                new int[]{7, 3},
                new int[]{3, 11},
                new int[]{9, 8},
                new int[]{8, 10},
                new int[]{4, 3},
                new int[]{1, 5},
                new int[]{1, 5}
        };
        System.out.println(numberOfWeakCharacters(ints));
    }

    /**
     * 参考评论区答案：
     * 攻击力按降序排列，防御力按升序排列，遍历数组，每次记录最大防御力，每当最大的防御力大于当前，及其攻击力必然比当前攻击力大
     * 执行结果：通过
     * 执行用时：104 ms, 在所有 Java 提交中击败了32.66%的用户
     * 内存消耗：87 MB, 在所有 Java 提交中击败了5.24%的用户
     */
    public int numberOfWeakCharacters(int[][] properties) {
        int sum = 0;
        Arrays.sort(properties, (ints, t1) -> {
            if (ints[0] > t1[0]) return -1;
            else if (ints[0] < t1[0]) return 1;
            if (ints[1] > t1[1]) return 1;
            else if (ints[1] < t1[1]) return -1;
            return 0;
        });
        int defense = properties[0][1];
        for (int i = 1; i < properties.length; i++) {
            System.out.println(Arrays.toString(properties[i]));
            if (defense > properties[i][1]) sum++;
            defense = Math.max(defense, properties[i][1]);
        }
        return sum;
    }
}
