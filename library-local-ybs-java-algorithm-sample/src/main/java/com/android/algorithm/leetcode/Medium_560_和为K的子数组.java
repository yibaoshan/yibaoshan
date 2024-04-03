package com.android.algorithm.leetcode;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class Medium_560_和为K的子数组 {

    /**
     * 给你一个整数数组 nums 和一个整数 k ，请你统计并返回该数组中和为 k 的连续子数组的个数。
     * <p>
     *  
     * <p>
     * 示例 1：
     * <p>
     * 输入：nums = [1,1,1], k = 2
     * 输出：2
     * <p>
     * <p>
     * 来源：力扣（LeetCode）
     * 链接：https://leetcode-cn.com/problems/subarray-sum-equals-k
     * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
     */

    @Test
    public void main() {
        int[] nums = new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        System.out.println(subarraySum3(nums, 9));
        System.out.println(subarraySum4(nums, 9));
    }

    /**
     * 抄的官方版，类似动态规划吧
     * 执行结果：通过
     * 执行用时：1410 ms, 在所有 Java 提交中击败了22.91%的用户
     * 内存消耗：41.3 MB, 在所有 Java 提交中击败了34.64%的用户
     */
    public int subarraySum4(int[] nums, int k) {
        int count = 0, pre = 0;
        int len = nums.length;
        HashMap<Integer, Integer> map = new HashMap<>();
        map.put(0, 1);
        for (int i = 0; i < len; i++) {
            pre += nums[i];
            if (map.containsKey(pre - k)) {
                count += map.get(pre - k);
            }
            map.put(pre, map.getOrDefault(pre, 0) + 1);
        }
        return count;
    }

    /**
     * 暴力解法
     * 执行结果：通过
     * 执行用时：1410 ms, 在所有 Java 提交中击败了22.91%的用户
     * 内存消耗：41.3 MB, 在所有 Java 提交中击败了34.64%的用户
     */
    public int subarraySum3(int[] nums, int k) {
        int count = 0;
        int index = 0;
        int len = nums.length;
        while (index < len) {
            int diff = k;
            for (int i = index; i < nums.length; i++) {
                diff = diff - nums[i];
                if (diff == 0) {
                    count++;
                }
            }
            index++;
        }
        return count;
    }

    //改进版 未通过
    public int subarraySum2(int[] nums, int k) {
        if (nums == null || nums.length == 0) return 0;
        int count = 0;
        HashMap<Integer, Integer> hashMap = new HashMap<>();
        for (int i = 0; i < nums.length; i++) {
            int cur = nums[i];
            int diff = k - cur;
            if (cur == k || hashMap.containsValue(cur)) {
                count++;
                hashMap.clear();
            }
            for (Map.Entry<Integer, Integer> next : hashMap.entrySet()) {
                next.setValue(next.getValue() - cur);
                if (next.getValue() < 0) {
                    hashMap.clear();
                    break;
                }
            }
            hashMap.put(cur, diff);
        }
        return count;
    }

    //最初版 未通过
    public int subarraySum(int[] nums, int k) {
        if (nums == null || nums.length == 0) return 0;
        int sum = 0, count = 0;
        for (int i = 0; i < nums.length; i++) {
            int cur = nums[i];
            if (cur == k) {
                count++;
                sum = 0;
                continue;
            }
            sum += cur;
            if (sum == k) {
                count++;
                sum = 0;
            } else if (sum > k) {
                sum = 0;
            }
        }
        return count;
    }

}
