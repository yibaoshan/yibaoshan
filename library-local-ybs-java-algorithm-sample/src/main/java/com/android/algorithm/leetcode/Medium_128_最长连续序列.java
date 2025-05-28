package com.android.algorithm.leetcode;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Medium_128_最长连续序列 {

    /**
     * 给定一个未排序的整数数组 nums ，找出数字连续的最长序列（不要求序列元素在原数组中连续）的长度。
     * <p>
     * 请你设计并实现时间复杂度为 O(n) 的算法解决此问题。
     */

    @Test
    public void main() {
    }

    /**
     * 忽略 O(n)，sort 搞定，管你这那的
     * 执行用时分布41ms击败14.75%
     * 消耗内存分布61.98MB击败76.99%
     * */
    public int longestConsecutive(int[] nums) {
        if (nums == null || nums.length == 0) return 0;
        Set<Integer> set = new HashSet<>();
        for (int n : nums) set.add(n);
        List<Integer> list = new ArrayList<>(set);
        Collections.sort(list);
        int ret = 1, max = 1;
        for (int i = 1; i < list.size(); i++) {
            if (list.get(i) - list.get(i - 1) == 1) {
                max++;
            } else {
                ret = Math.max(ret, max);
                max = 1;
            }
        }
        ret = Math.max(ret, max);
        return ret;
    }

}
