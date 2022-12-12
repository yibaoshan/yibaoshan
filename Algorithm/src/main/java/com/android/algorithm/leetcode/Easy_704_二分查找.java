package com.android.algorithm.leetcode;

import org.junit.Test;

public class Easy_704_二分查找 {

    /**
     * 给定一个 n 个元素有序的（升序）整型数组 nums 和一个目标值 target  ，写一个函数搜索 nums 中的 target，如果目标值存在返回下标，否则返回 -1。
     * <p>
     * <p>
     * 示例 1:
     * <p>
     * 输入: nums = [-1,0,3,5,9,12], target = 9
     * 输出: 4
     * 解释: 9 出现在 nums 中并且下标为 4
     * 示例 2:
     * <p>
     * 输入: nums = [-1,0,3,5,9,12], target = 2
     * 输出: -1
     * 解释: 2 不存在 nums 中因此返回 -1
     *  
     * <p>
     * 提示：
     * <p>
     * 你可以假设 nums 中的所有元素是不重复的。
     * n 将在 [1, 10000]之间。
     * nums 的每个元素都将在 [-9999, 9999]之间。
     * <p>
     * 来源：力扣（LeetCode）
     * 链接：https://leetcode-cn.com/problems/binary-search
     * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
     */

    @Test
    public void main() {
        int[] nums = new int[]{-1, 0, 3, 5, 9, 12};
        int target = 5;
        System.out.println(search(nums, target));
        System.out.println(search2(nums, target));
    }

    public int search2(int[] nums, int target) {
        if (nums == null || nums.length < 1) return -1;
        if (nums.length == 1) return nums[0] == target ? 0 : -1;
        int left = 0, right = nums.length - 1; // 左右指针分别指向，数组最左边，最右边
        while (left <= right) {
            int mid = (left + right) / 2;
            if (nums[mid] > target) right = mid - 1; // 中间值大于目标值，说明目标在中间值的左边👈🏻
            else if (nums[mid] < target) left = mid + 1; // 中间值小于目标值，说明目标在中间值的右边👉🏻
            else return mid;
        }
        return -1; // 时间复杂度 O(log2 n) 空间复杂度 O(1)
    }

    /**
     * 执行结果：通过
     * 执行用时：0 ms, 在所有 Java 提交中击败了100.00%的用户
     * 内存消耗：39.4 MB, 在所有 Java 提交中击败了35.80%的用户
     */
    public int search(int[] nums, int target) {
        if (nums == null && nums.length == 0) return -1;
        int left = 0, right = nums.length - 1, mid = right / 2;
        while (left <= right) {
            if (target > nums[mid]) left = mid + 1;
            else if (target < nums[mid]) right = mid - 1;
            else return mid;
            mid = (right + left) / 2;
        }
        return -1;
    }

}
