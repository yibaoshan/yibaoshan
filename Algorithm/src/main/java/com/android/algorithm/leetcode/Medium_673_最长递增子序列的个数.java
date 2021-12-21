package com.android.algorithm.leetcode;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Medium_673_最长递增子序列的个数 {

    /**
     * 给定一个未排序的整数数组，找到最长递增子序列的个数。
     * <p>
     * 示例 1:
     * <p>
     * 输入: [1,3,5,4,7]
     * 输出: 2
     * 解释: 有两个最长递增子序列，分别是 [1, 3, 4, 7] 和[1, 3, 5, 7]。
     * <p>
     * 来源：力扣（LeetCode）
     * 链接：https://leetcode-cn.com/problems/number-of-longest-increasing-subsequence
     * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
     */

    @Test
    public void main() {
        int[] nums = new int[]{1, 3, 5, 4, 7};
        System.out.println(findNumberOfLIS(nums));
    }

    public int findNumberOfLIS(int[] nums) {
        if (nums == null || nums.length == 0) return 0;
        int maxLen = 0, res = 0;
        int[] dp = new int[nums.length];
        int[] count = new int[nums.length];
        for (int i = 0; i < nums.length; ++i) {
            dp[i] = 1;
            count[i] = 1;
            for (int j = 0; j < i; ++j) {
                if (nums[i] > nums[j]) {
                    if (dp[j] + 1 > dp[i]) {
                        dp[i] = dp[j] + 1;
                        count[i] = count[j]; // 重置计数
                    } else if (dp[j] + 1 == dp[i]) {
                        count[i] += count[j];
                    }
                }
            }
            if (dp[i] > maxLen) {
                maxLen = dp[i];
                res = count[i]; // 重置计数
            } else if (dp[i] == maxLen) {
                res += count[i];
            }
        }
        return res;
    }

}
