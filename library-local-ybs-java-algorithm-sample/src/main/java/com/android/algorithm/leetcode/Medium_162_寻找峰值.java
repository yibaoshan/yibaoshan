package com.android.algorithm.leetcode;

import org.junit.Test;

public class Medium_162_寻找峰值 {

    /**
     * 峰值元素是指其值严格大于左右相邻值的元素。
     * <p>
     * 给你一个整数数组 nums，找到峰值元素并返回其索引。数组可能包含多个峰值，在这种情况下，返回 任何一个峰值 所在位置即可。
     * <p>
     * 你可以假设 nums[-1] = nums[n] = -∞ 。
     * <p>
     * 你必须实现时间复杂度为 O(log n) 的算法来解决此问题。
     * <p>
     * 来源：力扣（LeetCode）
     * 链接：https://leetcode-cn.com/problems/find-peak-element
     * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
     */

    @Test
    public void main() {
//        int[] nums = new int[]{1};
        int[] nums = new int[]{1, 2, 1, 3, 5, 6, 4};
//        int[] nums = new int[]{1,3,5,7,9,10,1};
        System.out.println(findPeakElement(nums));
    }

    //当前比右边小，那么肯定在右边会有峰顶，最差情况走到头，也是一个峰顶；
    // 如果当前比左边小，那么肯定在左边会有峰顶，最差情况一路走到头，也是一个峰顶。

    /**
     * 二分查找
     * 执行结果：通过
     * 执行用时：0 ms, 在所有 Java 提交中击败了100.00%的用户
     * 内存消耗：38.2 MB, 在所有 Java 提交中击败了23.48%的用户
     */
    public int findPeakElement(int[] nums) {
        if (nums == null) return -1;
        if (nums.length == 1) return 0;
        int left = 0, right = nums.length - 1, mid = (left + right) / 2;
        while (left != mid && right != mid) {
            if (nums[mid] < nums[mid + 1]) {
                left = mid;
            } else {
                right = mid;
            }
            mid = (left + right) / 2;
        }
        mid = nums[left] > nums[right] ? left : right;
        return mid;
    }

}
