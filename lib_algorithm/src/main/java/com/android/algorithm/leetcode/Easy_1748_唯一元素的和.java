package com.android.algorithm.leetcode;

import org.junit.Test;

import java.util.HashMap;
import java.util.HashSet;

public class Easy_1748_唯一元素的和 {

    /**
     * 给你一个整数数组 nums 。数组中唯一元素是那些只出现 恰好一次 的元素。
     * <p>
     * 请你返回 nums 中唯一元素的 和 。
     */
    @Test
    public void main() {
        int[] nums = new int[]{1,1,1,1,1};
        System.out.println(sumOfUnique(nums));
    }

    /**
     * 执行结果：通过
     * 执行用时：1 ms, 在所有 Java 提交中击败了55.82%的用户
     * 内存消耗：39.4 MB, 在所有 Java 提交中击败了5.13%的用户
     */
    public int sumOfUnique(int[] nums) {
        if (nums == null || nums.length == 0) return 0;
        int sum = 0;
        HashMap<Integer, Boolean> hashMap = new HashMap<>();
        for (int num : nums) {
            if (hashMap.containsKey(num)) {
                if (hashMap.get(num)) sum -= num;
                hashMap.put(num, false);
            } else {
                hashMap.put(num, true);
                sum += num;
            }
        }
        return sum;
    }

}
