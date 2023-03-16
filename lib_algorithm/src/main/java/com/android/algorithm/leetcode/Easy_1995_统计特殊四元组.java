package com.android.algorithm.leetcode;

import org.junit.Test;

public class Easy_1995_统计特殊四元组 {

    /**
     * 给你一个 下标从 0 开始 的整数数组 nums ，返回满足下述条件的 不同 四元组 (a, b, c, d) 的 数目 ：
     * <p>
     * nums[a] + nums[b] + nums[c] == nums[d] ，且
     * a < b < c < d
     * <p>
     * <p>
     * 来源：力扣（LeetCode）
     * 链接：https://leetcode-cn.com/problems/count-special-quadruplets
     * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
     */

    @Test
    public void main() {
        int[] nums = new int[]{1, 2, 3, 6};
        System.out.println(countQuadruplets(nums));
    }

    public int countQuadruplets(int[] nums) {
        if (nums == null || nums.length == 0) return 0;
        int n = nums.length, res = 0;
        int[] cnt = new int[10010];
        for (int b = n - 3; b >= 1; b--) {
            for (int d = b + 2; d < n; d++) cnt[nums[d] - nums[b + 1] + 200]++;
            for (int a = 0; a < b; a++) res += cnt[nums[a] + nums[b] + 200];
        }
        return res;
    }

}
