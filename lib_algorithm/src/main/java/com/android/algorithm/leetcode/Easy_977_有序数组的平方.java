package com.android.algorithm.leetcode;

import org.junit.Test;

import java.util.Arrays;
import java.util.Comparator;

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
//        int[] nums = new int[]{-4, -1, 0, 3, 10};
        int[] nums = new int[]{-5, -3, -2, -1};
        System.out.println(Arrays.toString(nums));
        System.out.println(Arrays.toString(sortedSquares(nums)));
        System.out.println(Arrays.toString(sortedSquares2(nums)));
        System.out.println(Arrays.toString(sortedSquares3(nums)));
    }

    public int[] sortedSquares3(int[] nums) {
        /**
         * 题目很简单，我们可以先平方，再排序，这样的时间复杂度是 O(n+logn)
         *
         * 但是，进阶版本问我能不能把时间复杂度控制在 O(n)，那必须可以啊
         *
         * 根据题目描述我们发现，给到的数组是升序的，包含正数也包含负数
         *
         * 也就是说，最左侧的负数平方值可能比最右侧正数平方值要大
         *
         * 那我们可以继续使用双指针，指向最左和最右
         *
         * 每次遍历把左右指针的值拿出来比较，最大的数交换到右边，这样一次遍历后，排序也就排好了
         *
         * */
        if (nums == null || nums.length < 1) return nums;
        int len = nums.length, left = 0, right = len - 1;
        int[] ret = new int[len];
        while (--len >= 0) {
            // 算出左右指针各自的平方
            int leftVal = (int) Math.pow(nums[left], 2);
            int rightVal = (int) Math.pow(nums[right], 2);

            ret[len] = Math.max(leftVal, rightVal); // 从右到左，存入左右指针最大值

            if (leftVal > rightVal) left++;
            else right--;
        }
        return ret;
    }

    private void swap(int[] nums, int i1, int i2) {
        nums[i1] = nums[i1] ^ nums[i2];
        nums[i2] = nums[i1] ^ nums[i2];
        nums[i1] = nums[i1] ^ nums[i2];
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
