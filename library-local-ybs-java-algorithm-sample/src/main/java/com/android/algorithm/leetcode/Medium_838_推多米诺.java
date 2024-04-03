package com.android.algorithm.leetcode;

import org.junit.Test;

import java.util.Arrays;

public class Medium_838_推多米诺 {

    @Test
    public void main() {
//        String dominoes = "RR.L";
        String dominoes = ".L.R...LR..L..";
//        String dominoes = "..R..";
//        System.out.println("LL.RR.LLRRLL..");
        System.out.println(pushDominoes(dominoes));
    }

    /**
     * 两轮遍历Java版预处理+模拟
     *
     * 1. 创建左、右两个权重数组，一轮遍历，记录每张站着的多米诺骨牌([i] = '.')左右受力的情况
     * 以'.L.R...LR..L..'举例，遍历完成后的受力情况是
     * left[10, 0, 0, 0, 8, 9, 10, 0, 0, 9, 10, 0, 0, 0]
     * right[0, 0, 0, 0, 10, 9, 8, 0, 0, 10, 9, 0, 0, 0]
     * 2. 二轮遍历，根据受力权重改变站着的多米诺骨牌的方向，若左右受力相同，不动就行了
     * 执行结果：通过
     * 执行用时：13 ms, 在所有 Java 提交中击败了75.68%的用户
     * 内存消耗：43.7 MB, 在所有 Java 提交中击败了13.06%的用户
     */
    public String pushDominoes(String dominoes) {
        char[] chars = dominoes.toCharArray();
        int[] left = new int[chars.length];
        int[] right = new int[chars.length];
        for (int i = 0; i < chars.length; i++) {
            if (chars[i] == '.') continue;
            int curIndex = i;
            int weight = Integer.MAX_VALUE;
            if (chars[i] == 'L') {
                while (--curIndex >= 0) {
                    if (chars[curIndex] != '.') break;
                    left[curIndex] += weight--;
                }
            } else if (chars[i] == 'R') {
                while (++curIndex < chars.length) {
                    if (chars[curIndex] != '.') break;
                    right[curIndex] += weight--;
                }
            }
        }
        for (int i = 0; i < chars.length; i++) {
            if (chars[i] != '.') continue;
            if (left[i] > right[i]) chars[i] = 'L';
            else if (left[i] < right[i]) chars[i] = 'R';
        }
        System.out.println(Arrays.toString(left));
        System.out.println(Arrays.toString(right));
        return new String(chars);
    }

}
