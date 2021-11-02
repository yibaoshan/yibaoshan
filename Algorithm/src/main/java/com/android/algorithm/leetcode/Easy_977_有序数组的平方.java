package com.android.algorithm.leetcode;

import org.junit.Test;

import java.util.Arrays;

public class Easy_977_有序数组的平方 {

    /**
     * 给你一个按 非递减顺序 排序的整数数组 nums，返回 每个数字的平方 组成的新数组，要求也按 非递减顺序 排序。
     * <p>
     *  
     * <p>
     * 示例 1：
     * <p>
     * 输入：nums = [-4,-1,0,3,10]
     * 输出：[0,1,9,16,100]
     * 解释：平方后，数组变为 [16,1,0,9,100]
     * 排序后，数组变为 [0,1,9,16,100]
     * 示例 2：
     * <p>
     * 输入：nums = [-7,-3,2,3,11]
     * 输出：[4,9,9,49,121]
     *  
     * <p>
     * 提示：
     * <p>
     * 1 <= nums.length <= 104
     * -104 <= nums[i] <= 104
     * nums 已按 非递减顺序 排序
     *  
     * <p>
     * 进阶：
     * <p>
     * 请你设计时间复杂度为 O(n) 的算法解决本问题
     * <p>
     * <p>
     * 来源：力扣（LeetCode）
     * 链接：https://leetcode-cn.com/problems/squares-of-a-sorted-array
     * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
     */

    @Test
    public void main() {
        int[] nums = new int[]{-4, -1, 0, 3, 10};
        System.out.println(Arrays.toString(nums));
        System.out.println(Arrays.toString(sortedSquares(nums)));
        System.out.println(Arrays.toString(sortedSquares2(nums)));
    }

    /**
     * 执行结果：通过
     * 执行用时：2 ms, 在所有 Java 提交中击败了41.38%的用户
     * 内存消耗：40.1 MB, 在所有 Java 提交中击败了71.06%的用户
     */
    public int[] sortedSquares(int[] nums) {
        if (nums == null) return null;
        int[] result = new int[nums.length];
        for (int i = 0; i < nums.length; i++) {
            result[i] = (int) Math.pow(nums[i], 2);
        }
        Arrays.sort(result);
        return result;
    }

    /**
     * 双指针
     * 执行结果：通过
     * 执行用时：2 ms, 在所有 Java 提交中击败了41.38%的用户
     * 内存消耗：40.1 MB, 在所有 Java 提交中击败了74.37%的用户
     */
    public int[] sortedSquares2(int[] nums) {
        if (nums == null) return null;
        int[] result = new int[nums.length];
        int left = 0, right = result.length - 1;
        for (int i = nums.length - 1; i >= 0; i--) {
            int leftNum = (int) Math.pow(nums[left], 2);
            int rightNum = (int) Math.pow(nums[right], 2);
            if (leftNum > rightNum) {
                result[i] = leftNum;
                left++;
            } else {
                result[i] = rightNum;
                right--;
            }
        }
        return result;
    }

}
