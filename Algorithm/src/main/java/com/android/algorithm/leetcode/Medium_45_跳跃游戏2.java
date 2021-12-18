package com.android.algorithm.leetcode;

import org.junit.Test;

public class Medium_45_跳跃游戏2 {

    /**
     * 给你一个非负整数数组 nums ，你最初位于数组的第一个位置。
     * <p>
     * 数组中的每个元素代表你在该位置可以跳跃的最大长度。
     * <p>
     * 你的目标是使用最少的跳跃次数到达数组的最后一个位置。
     * <p>
     * 假设你总是可以到达数组的最后一个位置。
     * <p>
     * 来源：力扣（LeetCode）
     * 链接：https://leetcode-cn.com/problems/jump-game-ii
     * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
     */

    @Test
    public void main() {
        int[] nums = new int[]{1, 2};
        System.out.println(jump(nums));
    }

    /**
     * 动态规划，评论区答案
     * 执行结果：通过
     * 执行用时：2 ms, 在所有 Java 提交中击败了47.33%的用户
     * 内存消耗：39.3 MB, 在所有 Java 提交中击败了31.18%的用户
     */
    public int jump(int[] nums) {
        if (nums == null || nums.length < 2) return 0;
        int count = 0;
        if (nums.length == 1) return 0;
        int val = 0, next = nums[0];
        for (int i = 0; i < nums.length; i++) {
            next = Math.max(i + nums[i], next);
            if (next >= nums.length - 1) return ++count;
            if (i == val) {
                count++;
                val = next;
            }
        }
        return count;
    }

}
