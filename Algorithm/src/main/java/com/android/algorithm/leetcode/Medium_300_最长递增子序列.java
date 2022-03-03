package com.android.algorithm.leetcode;

import org.junit.Test;

import java.util.Arrays;

public class Medium_300_最长递增子序列 {

    /**
     * 给你一个整数数组 nums ，找到其中最长严格递增子序列的长度。
     * <p>
     * 子序列是由数组派生而来的序列，删除（或不删除）数组中的元素而不改变其余元素的顺序。例如，[3,6,2,7] 是数组 [0,3,1,6,2,2,7] 的子序列。
     * <p>
     * 来源：力扣（LeetCode）
     * 链接：https://leetcode-cn.com/problems/longest-increasing-subsequence
     * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
     */
    @Test
    public void main() {
//        int[] nums = new int[]{10, 9, 2, 5, 3, 7, 101, 18};
        int[] nums = new int[]{1, 3, 6, 7, 9, 4, 10, 5, 6};
        System.out.println(lengthOfLIS(nums));
        System.out.println(lengthOfLIS2(nums));
    }

    /**
     * 执行结果：通过
     * 执行用时：61 ms, 在所有 Java 提交中击败了61.34%的用户
     * 内存消耗：38.1 MB, 在所有 Java 提交中击败了51.02%的用户
     */
    public int lengthOfLIS(int[] nums) {
        if (nums == null || nums.length == 0) return 0;
        int[] dp = new int[nums.length];
        int res = 0;
        Arrays.fill(dp, 1);
        for (int i = 0; i < nums.length; i++) {
            for (int j = 0; j < i; j++) {
                if (nums[j] < nums[i]) dp[i] = Math.max(dp[i], dp[j] + 1);
            }
            res = Math.max(res, dp[i]);
        }
        return res;
    }

    /**
     * 1
     * 2
     * 3
     * 4
     * 5
     * 6
     * 7
     * 8
     * 9
     * 0
     * 1
     * 2
     * 3
     * 4
     * 5
     * 6
     * 7
     * 8
     * 9
     * 0
     */
    /**
     * dp思路：
     * dp[i]=i之前的最长递增子序列，如何理解呢？
     * 我们以官方给的例子来举例：10, 9, 2, 5, 3, 7, 101, 18
     * 前提：两层循环
     * 第一层循环从0到数组长度，也就是遍历数组
     * 第二层循环，从0到第i个，目的是找从0到i中最长的递增子序列
     * 判断条件是num[i]>num[j]，符合条件的dp公式是max(dp[i],dp[j]+1)
     * 我们一步步拆解来看
     *
     * 当i=0时，0~0之间没有递增数字，跳过
     * 当前dp数组=[0,0,0,0,0,0,0,0]
     *
     * 当i=1时值是9，0~1之间是10、9，根据num[i]>num[j]，9要和9之前所有数字对比，显然9没法大于10
     * 当前dp数组=[0,0,0,0,0,0,0,0]
     *
     * 当i=2时值是2，2要和10、9比较，根据num[i]>num[j]，2不会大于2之前的任意一个数
     * 当前dp数组=[0,0,0,0,0,0,0,0]
     *
     * 当i=3时值是5，5要和0~3之间的10、9、2比较，5大于2，2的下标是j=2，那么dp[3] = max(dp[i],dp[2]+1) = 1
     * 当前dp数组=[0,0,0,1,0,0,0,0]
     *
     * 当i=4时值是3，3要和0~4之间的10、9、2、5比较，3大于2，2的下标是j=2，和上面情况一样，dp[4] = max(dp[i],dp[2]+1) = 1
     * 当前dp数组=[0,0,0,1,1,0,0,0]
     *
     * 当i=5时值是7，7要和0~5之间的10、9、2、5、3，7大于2、5、3，我们一个个来看看：
     * 当7>2时，此时的dp[i=5]=0，dp[j=2]=0，根据公式max(dp[i],dp[j]+1)，此时dp[i] = dp[j=2]+1 = 1
     * 当7>5时，此时的dp[i=5]=1，dp[j=3]=1，根据max(dp[i],dp[j]+1)，此时dp[i] = dp[j=3]+1 = 1+1 = 2
     * 当7>3时，此时的dp[i=5]=1，dp[j=4]=1，根据max(dp[i],dp[j]+1)，此时dp[i] = dp[j=4]+1 = 1+1 = 2
     * 当前dp数组=[0,0,0,1,1,2,0,0]
     *
     * 当i=6时值是101，101要和0~6之间的10、9、2、5、3、7比较比较，101大于前面所有数字，所以我们又要一个个来看看：
     * 当101>10时，此时的dp[i=6]=0，dp[j=0]=0，根据公式max(dp[i],dp[j]+1)，此时dp[i] = dp[j=0]+1 = 1
     * 当101>9时，此时的dp[i=6]=1，dp[j=1]=0，根据公式max(dp[i],dp[j]+1)，此时dp[i] = dp[j=1]+1 = 1
     * 当101>2时，此时的dp[i=6]=1，dp[j=2]=0，根据公式max(dp[i],dp[j]+1)，此时dp[i] = dp[j=2]+1 = 1
     * 当101>5时，此时的dp[i=6]=1，dp[j=3]=1，根据公式max(dp[i],dp[j]+1)，此时dp[i] = dp[j=3]+1 = 1+1 = 2
     * 当101>3时，此时的dp[i=6]=2，dp[j=4]=1，根据公式max(dp[i],dp[j]+1)，此时dp[i] = dp[j=4]+1 = 1+1 = 2
     * 当101>7时，此时的dp[i=6]=2，dp[j=5]=2，根据公式max(dp[i],dp[j]+1)，此时dp[i] = dp[j=5]+1 = 2+1 = 3
     * 当前dp数组=[0,0,0,1,1,2,3,0]
     *
     */
    /**
     * 执行结果：通过
     * 执行用时：59 ms, 在所有 Java 提交中击败了49.72%的用户
     * 内存消耗：41.3 MB, 在所有 Java 提交中击败了5.03%的用户
     */
    public int lengthOfLIS2(int[] nums) {
        int[] dp = new int[nums.length];
        Arrays.fill(dp, 1);
        for (int i = 0; i < nums.length; i++) {
            for (int j = 0; j < i; j++) {
                if (nums[i] > nums[j]) dp[i] = Math.max(dp[i], dp[j] + 1);
            }
        }
        Arrays.sort(dp);
        return dp[dp.length - 1];
    }

}
