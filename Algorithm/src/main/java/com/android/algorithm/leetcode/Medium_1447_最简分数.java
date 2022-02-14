package com.android.algorithm.leetcode;

import org.junit.Test;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.List;

public class Medium_1447_最简分数 {

    /**
     * 给你一个整数 n ，请你返回所有 0 到 1 之间（不包括 0 和 1）满足分母小于等于  n 的 最简 分数 。分数可以以 任意 顺序返回。
     */

    @Test
    public void main() {
        int n = 3;
        List<String> list = simplifiedFractions(n);
        List<String> list2 = simplifiedFractions2(n);
        System.out.println(list.size());
        System.out.println(list2.size());
    }

    /**
     * 回溯
     * 执行结果：通过
     * 执行用时：51 ms, 在所有 Java 提交中击败了6.38%的用户
     * 内存消耗：42.4 MB, 在所有 Java 提交中击败了5.68%的用户
     */
    public List<String> simplifiedFractions(int n) {
        backtrack(1, n);
        List<String> list = new ArrayList<>();
        for (int i = 0; i < res.size(); i++) {
            if (maxNumber(res.get(i).get(0), res.get(i).get(1)) == 1) list.add(res.get(i).get(0) + "/" + res.get(i).get(1));
        }
        return list;
    }

    private final Deque<Integer> deque = new ArrayDeque<>();
    private final List<List<Integer>> res = new ArrayList<>();

    private void backtrack(int start, int n) {
        if (deque.size() == 2) {
            res.add(new ArrayList<>(deque));
            return;
        }
        for (int i = start; i <= n; i++) {
            deque.addLast(i);
            backtrack(i + 1, n);
            deque.removeLast();
        }
    }

    private int maxNumber(int m, int n) {
        int temp;
        if (n > m) {
            temp = n;
            n = m;
            m = temp;
        }
        if (m % n == 0) {
            return n;
        }
        return maxNumber(m - n, n);
    }

    /**
     * 回溯
     * 执行结果：通过
     * 执行用时：19 ms, 在所有 Java 提交中击败了84.75%的用户
     * 内存消耗：42.1 MB, 在所有 Java 提交中击败了13.12%的用户
     */
    public List<String> simplifiedFractions2(int n) {
        List<String> list = new ArrayList<>();
        for (int i = 2; i <= n; i++) {
            for (int j = 1; j < n; j++) {
                if (i == j) continue;
                if (j >= i) break;
                if (gcd(i, j) == 1) list.add(j + "/" + i);
            }
        }
        return list;
    }

    public int gcd(int a, int b) {
        return b == 0 ? a : gcd(b, a % b);
    }

}
