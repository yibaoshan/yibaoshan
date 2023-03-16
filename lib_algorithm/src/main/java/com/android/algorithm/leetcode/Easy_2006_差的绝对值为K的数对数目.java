package com.android.algorithm.leetcode;

import org.junit.Test;

import java.util.HashMap;

public class Easy_2006_差的绝对值为K的数对数目 {

    /**
     * 给你一个整数数组 nums 和一个整数 k ，请你返回数对 (i, j) 的数目，满足 i < j 且 |nums[i] - nums[j]| == k 。
     * <p>
     * |x| 的值定义为：
     * <p>
     * 如果 x >= 0 ，那么值为 x 。
     * 如果 x < 0 ，那么值为 -x 。
     * <p>
     * <p>
     * 来源：力扣（LeetCode）
     * 链接：https://leetcode-cn.com/problems/count-number-of-pairs-with-absolute-difference-k
     * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
     */

    @Test
    public void main() {

    }

    public int countKDifference(int[] nums, int k) {
        if (nums == null || nums.length == 0) return 0;
        HashMap<Integer, Integer> hashMap = new HashMap<>();
        int sum = 0;
        for (int num : nums) {
            if (hashMap.containsKey(num + k)) sum += hashMap.get(num + k);
            if (hashMap.containsKey(num - k)) sum += hashMap.get(num - k);
            hashMap.put(num, hashMap.getOrDefault(num, 0) + 1);
        }
        return sum;
    }

}
