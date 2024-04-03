package com.android.algorithm.leetcode;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Medium_1414_和为K的最少斐波那契数字数目 {

    @Test
    public void main() {
        System.out.println(findMinFibonacciNumbers(9));
    }

    /**
     * 评论区答案
     * 执行结果：通过
     * 执行用时：1 ms, 在所有 Java 提交中击败了83.43%的用户
     * 内存消耗：39 MB, 在所有 Java 提交中击败了5.15%的用户
     */
    public int findMinFibonacciNumbers(int k) {
        List<Integer> list = new ArrayList<>();
        int a = 1, b = 1, c = 2;
        list.add(a);
        list.add(b);
        list.add(c);
        while (c < k) {
            a = b;
            b = c;
            c = a + b;
            list.add(c);
        }
        int ans = 0;
        for (int i = list.size() - 1; i >= 0; i--) {
            if (k >= list.get(i)) {
                k -= list.get(i);
                ans++;
            }
        }
        return ans;
    }

}
