package com.android.algorithm.leetcode;

import org.junit.Test;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.List;

public class Medium_416_分割等和子集 {

    /**
     * 给你一个 只包含正整数 的 非空 数组 nums 。请你判断是否可以将这个数组分割成两个子集，使得两个子集的元素和相等。
     * <p>
     * 示例 1：
     * <p>
     * 输入：nums = [1,5,11,5]
     * 输出：true
     * 解释：数组可以分割成 [1, 5, 5] 和 [11] 。
     * <p>
     * 来源：力扣（LeetCode）
     * 链接：https://leetcode-cn.com/problems/partition-equal-subset-sum
     * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
     */

    @Test
    public void main() {
        int[] nums = new int[]{1, 5, 11, 5};
        System.out.println(canPartition(nums));
        System.out.println(canPartition2(nums));
        System.out.println(canPartition3(nums));
    }

    /**
     * 随想录答案
     * 动态规划：
     * 1. dp[j]：背包容量=j ，最大可以凑成j的子集总和为dp[j]
     * 2. 公式：dp[j] = max(dp[j], (dp[j - nums[i]]) + nums[i]);
     * 3. 初始化：
     * 执行结果：通过
     * 执行用时：19 ms, 在所有 Java 提交中击败了81.87%的用户
     * 内存消耗：37.9 MB, 在所有 Java 提交中击败了57.47%的用户
     */
    public boolean canPartition(int[] nums) {
        int sum = 0;
        for (int i = 0; i < nums.length; i++) sum += nums[i];
        if (sum % 2 != 0) return false;
        int target = sum / 2;
        int[] dp = new int[target + 1];
        for (int i = 0; i < nums.length; i++) {
            int cur = nums[i];
            for (int j = target; j >= cur; j--) {
                int diff = j - cur;
                int diffVal = dp[diff];
                dp[j] = Math.max(dp[j], diffVal + cur);
            }
        }
        return dp[target] == target;
    }

    /**
     * 回溯法：只要有组合的值等于sum/2，代表可以拆解成等和
     * 不通过，超时
     */
    public boolean canPartition2(int[] nums) {
        int sum = 0;
        for (int i = 0; i < nums.length; i++) sum += nums[i];
        if (sum % 2 != 0) return false;
        boolean[] visited = new boolean[nums.length];
        backtrack(nums, visited, sum / 2);
        return result;
    }

    private int sum = 0;
    private boolean result = false;

    private void backtrack(int[] nums, boolean[] visited, int target) {
        if (result) return;
        if (sum == target) {
            result = true;
            return;
        }
        for (int i = 0; i < nums.length; i++) {
            if (visited[i]) continue;
            visited[i] = true;
            sum += nums[i];
            backtrack(nums, visited, target);
            sum -= nums[i];
            visited[i] = false;
        }
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
     * 12
     * 3
     * <p>
     * 4
     * 5
     * 6
     * 7
     * 8
     * 9
     * 0
     */
    public boolean canPartition3(int[] nums) {
        if (nums == null || nums.length < 2) return false;
        int sum = 0;
        for (int i = 0; i < nums.length; i++) sum += nums[i];
        if (sum % 2 != 0) return false;
        sum /= 2;
        int[][] dp = new int[nums.length][sum];
        print(dp);
        return true;
    }

    private void print(int[][] ints) {
        for (int i = 0; i < ints.length; i++) {
            System.out.println(Arrays.toString(ints[i]));
        }
    }

    @Test
    public void test() {
        int[] weight = new int[]{1, 3, 4};
        int[] values = new int[]{15, 20, 30};
        int maxWeight = 4;
        System.out.println(bag(weight, values, maxWeight));
    }

    private int bag(int[] weight, int[] values, int maxWeight) {
        int[][] dp = new int[values.length][weight.length + 1];
        for (int i = 1; i < dp[0].length; i++) {
            dp[0][i] = values[0];
        }
        //dp[i][j] = 最大的价值
        for (int i = 0; i < dp.length; i++) {
            for (int j = 0; j < dp[i].length; j++) {
                print(dp);
            }
        }
        return 0;
    }

}
