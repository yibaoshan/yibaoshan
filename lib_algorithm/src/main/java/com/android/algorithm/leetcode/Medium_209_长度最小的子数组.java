package com.android.algorithm.leetcode;

import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Medium_209_长度最小的子数组 {

    /**
     * 给定一个含有 n 个正整数的数组和一个正整数 target 。
     * <p>
     * 找出该数组中满足其和 ≥ target 的长度最小的 连续子数组 [numsl, numsl+1, ..., numsr-1, numsr] ，并返回其长度。如果不存在符合条件的子数组，返回 0 。
     * <p>
     * 来源：力扣（LeetCode）
     * 链接：https://leetcode-cn.com/problems/minimum-size-subarray-sum
     * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
     */

    @Test
    public void main() {
        int[] nums = new int[]{2, 3, 1, 2, 4, 3};
        System.out.println(minSubArrayLen(7, nums));
    }

    /**
     * 滑动窗口解法，思路：
     * 1. 声明左指针和右指针，若左指针右指针之和大于目标值，左指针右移，否则，右指针右移
     * 2. 更新满足条件（大于目标值）的间隔长度，保存最小值即可
     *
     * 执行结果：通过
     * 执行用时：1 ms, 在所有 Java 提交中击败了99.99%的用户
     * 内存消耗：38.2 MB, 在所有 Java 提交中击败了86.13%的用户
     */
    public int minSubArrayLen(int target, int[] nums) {
        if (nums == null) return -1;
        int res = Integer.MAX_VALUE, sum = 0, left = 0, right = 0;
        while (right < nums.length) {
            sum += nums[right];
            while (sum >= target) {
                res = Math.min(res, right - left + 1);
                sum -= nums[left];
                left++;
            }
            right++;
        }
        return res == Integer.MAX_VALUE ? 0 : res;
    }

    public String frequencySort(String s) {
        if (s == null) return s;
        char[] chars = s.toCharArray();
        HashMap<Character, Integer> hashMap = new HashMap<>();
        for (char key : chars) {
            if (hashMap.containsKey(key)) {
                int count = hashMap.get(key);
                hashMap.put(key, ++count);
            } else hashMap.put(key, 1);
        }
        Set<Map.Entry<Character, Integer>> entries = hashMap.entrySet();
        List<Map.Entry<Character, Integer>> values = new ArrayList<>(entries);
        values.sort((integerIntegerEntry, t1) -> t1.getValue().compareTo(integerIntegerEntry.getValue()));
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < values.size(); i++) {
            int count = values.get(i).getValue();
            for (int j = 0; j < count; j++) {
                builder.append(values.get(i).getKey());
            }
        }
        return builder.toString();
    }


}
