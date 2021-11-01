package com.android.algorithm.leetcode;

import org.junit.Test;

public class Easy_35_搜索插入位置 {

    /**
     * 给定一个排序数组和一个目标值，在数组中找到目标值，并返回其索引。如果目标值不存在于数组中，返回它将会被按顺序插入的位置。
     * <p>
     * 请必须使用时间复杂度为 O(log n) 的算法。
     * <p>
     *  
     * <p>
     * 示例 1:
     * <p>
     * 输入: nums = [1,3,5,6], target = 5
     * 输出: 2
     * 示例 2:
     * <p>
     * 输入: nums = [1,3,5,6], target = 2
     * 输出: 1
     * 示例 3:
     * <p>
     * 输入: nums = [1,3,5,6], target = 7
     * 输出: 4
     * 示例 4:
     * <p>
     * 输入: nums = [1,3,5,6], target = 0
     * 输出: 0
     * 示例 5:
     * <p>
     * 输入: nums = [1], target = 0
     * 输出: 0
     * <p>
     * 来源：力扣（LeetCode）
     * 链接：https://leetcode-cn.com/problems/search-insert-position
     * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
     */
    @Test
    public void main() {

    }

    /**
     * 执行结果：通过
     * 执行用时：0 ms, 在所有 Java 提交中击败了100.00%的用户
     * 内存消耗：38.1 MB, 在所有 Java 提交中击败了41.82%的用户
     */
    public int searchInsert(int[] nums, int target) {
        int left = 0, right = nums.length - 1, mid = right / 2;
        while (left <= right) {
            if (target > nums[mid]) left = mid + 1;
            else if (target < nums[mid]) right = mid - 1;
            else return mid;
            mid = left + (right - left) / 2;
        }
        return mid;
    }

}
