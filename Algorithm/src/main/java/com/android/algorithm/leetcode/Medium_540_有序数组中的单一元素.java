package com.android.algorithm.leetcode;

import org.junit.Test;

public class Medium_540_有序数组中的单一元素 {

    /**
     * 给你一个仅由整数组成的有序数组，其中每个元素都会出现两次，唯有一个数只会出现一次。
     * <p>
     * 请你找出并返回只出现一次的那个数。
     * <p>
     * 你设计的解决方案必须满足 O(log n) 时间复杂度和 O(1) 空间复杂度。
     * <p>
     * 来源：力扣（LeetCode）
     * 链接：https://leetcode-cn.com/problems/single-element-in-a-sorted-array
     * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
     */

    @Test
    public void main() {
        int[] nums = new int[]{1, 1, 2, 3, 3, 4, 4, 8, 8};
//        int[] nums = new int[]{3, 3, 7, 7, 10, 11, 11};
        System.out.println(singleNonDuplicate(nums));
        System.out.println(singleNonDuplicate2(nums));
    }

    public int singleNonDuplicate(int[] nums) {
        if (nums == null || nums.length == 0) return 0;
        int res = 0;
        for (int i = 0; i < nums.length; i++) {
            res ^= nums[i];
        }
        return res;
    }

    public int singleNonDuplicate2(int[] nums) {
        if (nums == null || nums.length == 0) return 0;
        int left = 0, right = nums.length - 1;
        while (left < right) {
            int mid = (left + right) / 2;
            if (mid % 2 == 0) {//偶数下标
                if (mid + 1 < nums.length && nums[mid] == nums[mid + 1]) left = mid + 1;
                else right = mid;
            } else {
                if (mid - 1 >= 0 && nums[mid - 1] == nums[mid]) left = mid + 1;
                else right = mid - 1;
            }
        }
        return nums[left];
    }

}
