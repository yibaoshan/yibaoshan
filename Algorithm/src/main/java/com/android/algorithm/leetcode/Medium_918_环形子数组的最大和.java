package com.android.algorithm.leetcode;

import org.junit.Test;

public class Medium_918_环形子数组的最大和 {

    /**
     * 给定一个由整数数组 A 表示的环形数组 C，求 C 的非空子数组的最大可能和。
     * <p>
     * 在此处，环形数组意味着数组的末端将会与开头相连呈环状。（形式上，当0 <= i < A.length 时 C[i] = A[i]，且当 i >= 0 时 C[i+A.length] = C[i]）
     * <p>
     * 此外，子数组最多只能包含固定缓冲区 A 中的每个元素一次。（形式上，对于子数组 C[i], C[i+1], ..., C[j]，不存在 i <= k1, k2 <= j 其中 k1 % A.length = k2 % A.length）
     * <p>
     * 来源：力扣（LeetCode）
     * 链接：https://leetcode-cn.com/problems/maximum-sum-circular-subarray
     * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
     */

    @Test
    public void main() {

    }

    /**
     * 评论区答案，完全看不懂以及不理解什么是环形数组
     * 执行结果：通过
     * 执行用时：6 ms, 在所有 Java 提交中击败了49.09%的用户
     * 内存消耗：46.2 MB, 在所有 Java 提交中击败了11.39%的用户
     */
    public int maxSubarraySumCircular(int[] nums) {
        if (nums == null || nums.length == 0) return 0;
        if (nums.length == 1) return nums[0];
        int dpx = nums[0], len = nums.length, sum = nums[0];
        int max = dpx, min = Math.min(dpx, 0), dpm = min;
        for (int i = 1; i < len; i++) {
            dpx = Math.max(dpx + nums[i], nums[i]);
            max = Math.max(max, dpx);
            if (i < len - 1) {
                dpm = Math.min(dpm + nums[i], nums[i]);
                min = Math.min(min, dpm);
            }
            sum += nums[i];
        }
        return Math.max(max, sum - min);
    }

}
