package com.android.algorithm.leetcode;

import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;

public class Easy_L1_两数之和 {

    /**
     * 给定一个整数数组 nums 和一个整数目标值 target，请你在该数组中找出 和为目标值 target  的那 两个 整数，并返回它们的数组下标。
     * <p>
     * 你可以假设每种输入只会对应一个答案。但是，数组中同一个元素在答案里不能重复出现。
     * <p>
     * 你可以按任意顺序返回答案。
     * <p>
     * 来源：力扣（LeetCode）
     * 链接：https://leetcode-cn.com/problems/two-sum
     * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
     */

    @Test
    public void main() {
        int[] nums = new int[]{-1, -2, -3, -4, -5};
        int target = -8;
        System.out.println(Arrays.toString(twoSum(nums, target)));
    }

    /**
     * 执行结果：通过
     * 执行用时：2 ms, 在所有 Java 提交中击败了85.74%的用户
     * 内存消耗：38.7 MB, 在所有 Java 提交中击败了30.94%的用户
     */
    public int[] twoSum(int[] nums, int target) {
        if (nums == null) return null;
        HashMap<Integer, Integer> hashMap = new HashMap<>();
        for (int i = 0; i < nums.length; i++) {
            if (hashMap.containsKey(nums[i])) return new int[]{hashMap.get(nums[i]), i};
            hashMap.put(target - nums[i], i);
        }
        return null;
    }

}
