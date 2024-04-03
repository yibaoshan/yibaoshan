package com.android.algorithm.leetcode;

import org.junit.Test;

import java.util.Arrays;

public class Medium_238_除自身以外数组的乘积 {

    /**
     * 给你一个长度为 n 的整数数组 nums，其中 n > 1，返回输出数组 output ，其中 output[i] 等于 nums 中除 nums[i] 之外其余各元素的乘积。
     * <p>
     *  
     * <p>
     * 示例:
     * <p>
     * 输入: [1,2,3,4]
     * 输出: [24,12,8,6]
     * <p>
     * 来源：力扣（LeetCode）
     * 链接：https://leetcode-cn.com/problems/product-of-array-except-self
     * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
     */

    @Test
    public void main() {
        int[] nums = new int[]{1, 2, 3, 4};
        productExceptSelf(nums);
    }

    /**
     * 思路：左右累乘
     * 执行结果：通过
     * 执行用时：3 ms, 在所有 Java 提交中击败了5.59%的用户
     * 内存消耗：49.1 MB, 在所有 Java 提交中击败了45.40%的用户
     */
    public int[] productExceptSelf(int[] nums) {
        if (nums == null) return nums;
        int[] res = new int[nums.length];
        Arrays.fill(res, 1);
        int left = 1, right = 1;//left：从左边累乘，right：从右边累乘
        for (int i = 0; i < nums.length; i++) { //最终每个元素其左右乘积进行相乘得出结果
            res[i] *= left;
            left *= nums[i];
            res[nums.length - i - 1] *= right;
            right *= nums[nums.length - i - 1];
            System.out.println("left=" + left + ",right=" + right);
            System.err.println(Arrays.toString(nums) + "," + Arrays.toString(res));
        }
        return res;
    }

}
