package com.android.algorithm.leetcode;

import org.junit.Test;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Medium_454_四数相加2 {

    /**
     * 给你四个整数数组 nums1、nums2、nums3 和 nums4 ，数组长度都是 n ，请你计算有多少个元组 (i, j, k, l) 能满足：
     * <p>
     * 0 <= i, j, k, l < n
     * nums1[i] + nums2[j] + nums3[k] + nums4[l] == 0
     */

    @Test
    public void main() {
    }

    public int fourSumCount(int[] nums1, int[] nums2, int[] nums3, int[] nums4) {
        /**
         * 参考题解的答案实现，思路：
         * 将 4 个数组分成 2 组，嵌套循环，分别计算 2 组元素和，再利用 hash 做匹配
         *
         * 将分为 2 组后的实现，类似两数之和的思想
         * */
        HashMap<Integer, Integer> hashMap = new HashMap<>();
        int ret = 0, sum;
        for (int n1 : nums1)
            for (int n2 : nums2) {
                sum = n1 + n2;
                if (hashMap.containsKey(sum)) hashMap.put(sum, hashMap.get(sum) + 1);
                else hashMap.put(sum, 1);
            }

        for (int n3 : nums3)
            for (int n4 : nums4) {
                sum = -(n3 + n4);
                if (hashMap.containsKey(sum)) ret += hashMap.get(sum);
            }
        return ret;
    }

}
