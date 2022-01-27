package com.android.algorithm.leetcode;

import org.junit.Test;

import java.util.Arrays;

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
//        int[] nums = new int[]{1, 2};
//        int[] nums = new int[]{2, 3, 1, 1, 4};
        int[] nums = new int[]{2, 3, 0, 1, 4};
        System.out.println(jump(nums));
        System.out.println(jump2(nums));
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

    /**
     * 稀里糊涂就过了，貌似是贪心
     * 1. 初始化max，当前能达到的最大下标
     * 2. 遍历数组，next一直记录最大能到达的位置下标，判断当前遍历下标是否等于最大下标了
     * 2.1 是，判断是否已经到底了，没到底的话当前值赋值为next，计数++，继续遍历
     * 2.2 否，不用管，继续遍历，继续更新next最大达到的位置
     * <p>
     * 执行结果：通过
     * 执行用时：2 ms, 在所有 Java 提交中击败了45.64%的用户
     * 内存消耗：39.6 MB, 在所有 Java 提交中击败了5.19%的用户
     */
    public int jump2(int[] nums) {
        if (nums == null || nums.length < 2) return 0;
        int maxPosition = nums[0];
        int nextPosition = 0;
        int cnt = 1;
        for (int i = 1; i < nums.length; i++) {
            nextPosition = Math.max(nums[i] + i, nextPosition);
            if (i == maxPosition) {
                if (i != nums.length - 1) {
                    cnt++;
                    maxPosition = nextPosition;
                } else break;
            }
        }
        return cnt;
    }

}
