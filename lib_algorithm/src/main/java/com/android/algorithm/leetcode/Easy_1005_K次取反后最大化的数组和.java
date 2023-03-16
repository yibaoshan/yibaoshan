package com.android.algorithm.leetcode;

import org.junit.Test;

import java.util.Arrays;

public class Easy_1005_K次取反后最大化的数组和 {

    /**
     * 给你一个整数数组 nums 和一个整数 k ，按以下方法修改该数组：
     * <p>
     * 选择某个下标 i 并将 nums[i] 替换为 -nums[i] 。
     * 重复这个过程恰好 k 次。可以多次选择同一个下标 i 。
     * <p>
     * 以这种方式修改数组后，返回数组 可能的最大和 。
     * <p>
     * 来源：力扣（LeetCode）
     * 链接：https://leetcode-cn.com/problems/maximize-sum-of-array-after-k-negations
     * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
     */

    @Test
    public void main() {
        int[] nums = new int[]{-5, -6, -2, -8, 6, 9};
        int k = 4;
        System.out.println(largestSumAfterKNegations(nums, k));
    }

    public int largestSumAfterKNegations(int[] nums, int k) {
        int sum = 0;
        Arrays.sort(nums);
        int n = k;
        for (int i = 0; i < n; i++) {
            if (i < nums.length && nums[i] < 0) {
                nums[i] *= -1;
                k--;
            }
        }
        Arrays.sort(nums);
        while (k > 0) {
            nums[0] *= -1;
            k--;
        }
        for (int num : nums) sum += num;
        return sum;
    }

}
