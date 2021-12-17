package com.android.algorithm.leetcode;

import org.junit.Test;

public class Medium_213_打家劫舍2 {

    /**
     * 你是一个专业的小偷，计划偷窃沿街的房屋，每间房内都藏有一定的现金。这个地方所有的房屋都 围成一圈 ，这意味着第一个房屋和最后一个房屋是紧挨着的。同时，相邻的房屋装有相互连通的防盗系统，如果两间相邻的房屋在同一晚上被小偷闯入，系统会自动报警 。
     * <p>
     * 给定一个代表每个房屋存放金额的非负整数数组，计算你 在不触动警报装置的情况下 ，今晚能够偷窃到的最高金额。
     * <p>
     * <p>
     * 来源：力扣（LeetCode）
     * 链接：https://leetcode-cn.com/problems/house-robber-ii
     * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
     */

    @Test
    public void main() {
        int[] nums = new int[]{1, 3, 1, 3, 100};
        System.out.println(rob(nums));
    }

    /**
     * 动态规划
     * 保留上一次prev和上上次prevPrev的结果
     * 当前节点+上上次结果和上次结果比较，取最大值作为当前结果
     * 同时，上上次结果改为上次prev的值，上次prev更改为当前result值
     * 执行结果：通过
     * 执行用时：0 ms, 在所有 Java 提交中击败了100.00%的用户
     * 内存消耗：35.8 MB, 在所有 Java 提交中击败了57.13%的用户
     */
    public int rob(int[] nums) {
        if (nums == null || nums.length == 0) return 0;
        if (nums.length == 1) return nums[0];
        if (nums.length == 2) return Math.max(nums[0], nums[1]);
        if (nums.length == 3) return Math.max(Math.max(nums[0], nums[1]), Math.max(nums[1], nums[2]));
        return Math.max(search(nums, 0, nums.length - 1), search(nums, 1, nums.length));
    }

    private int search(int[] nums, int start, int end) {
        int prevPrevRes = nums[start];
        int prevRes = Math.max(prevPrevRes, nums[start + 1]);
        int res = Math.max(prevPrevRes, prevRes);
        for (int i = 2 + start; i < end; i++) {
            int cur = nums[i];
            res = Math.max(prevRes, cur + prevPrevRes);
            prevPrevRes = prevRes;
            prevRes = res;
        }
        return res;
    }

}
