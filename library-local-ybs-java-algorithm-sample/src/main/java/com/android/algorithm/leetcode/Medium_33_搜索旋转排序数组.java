package com.android.algorithm.leetcode;

import org.junit.Test;

import java.util.Arrays;

public class Medium_33_搜索旋转排序数组 {

    /**
     * 整数数组 nums 按升序排列，数组中的值 互不相同 。
     * <p>
     * 在传递给函数之前，nums 在预先未知的某个下标 k（0 <= k < nums.length）上进行了 旋转，使数组变为 [nums[k], nums[k+1], ..., nums[n-1], nums[0], nums[1], ..., nums[k-1]]（下标 从 0 开始 计数）。例如， [0,1,2,4,5,6,7] 在下标 3 处经旋转后可能变为 [4,5,6,7,0,1,2] 。
     * <p>
     * 给你 旋转后 的数组 nums 和一个整数 target ，如果 nums 中存在这个目标值 target ，则返回它的下标，否则返回 -1 。
     * <p>
     * 来源：力扣（LeetCode）
     * 链接：https://leetcode-cn.com/problems/search-in-rotated-sorted-array
     * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
     */

    @Test
    public void main() {
        int[] nums = new int[]{1, 2, 3, 4, 5, 6, 7, 8};
        int target = 3;
        System.out.println(search(nums, target));
    }

    /**
     * 二分查找，思路：
     * 1. 找到旋转下标
     * 2. 根据下标将数组分割成两端有序数组，分别进行二分查找
     * 执行结果：通过
     * 执行用时：0 ms, 在所有 Java 提交中击败了100.00%的用户
     * 内存消耗：37.8 MB, 在所有 Java 提交中击败了43.49%的用户
     */
    public int search(int[] nums, int target) {
        int rotate = -1;
        for (int i = 0; i < nums.length - 1; i++) {
            if (nums[i] > nums[i + 1]) {
                rotate = i;
                break;
            }
        }
        int res = binarySearch(Arrays.copyOf(nums, rotate + 1), target);
        if (res > -1) return res;
        res = binarySearch(Arrays.copyOfRange(nums, rotate + 1, nums.length), target);
        if (res > -1) return rotate + res + 1;
        return -1;
    }

    private int binarySearch(int[] nums, int target) {
        int left = 0, right = nums.length - 1;
        while (left <= right) {
            int midIndex = (left + right) / 2;
            if (target < nums[midIndex]) {
                right = --midIndex;
            } else if (target > nums[midIndex]) {
                left = ++midIndex;
            } else return midIndex;
        }
        return -1;
    }

}
