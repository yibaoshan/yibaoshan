package com.android.algorithm.leetcode;

import org.junit.Test;

import java.util.Arrays;

public class Medium_189_旋转数组 {

    /**
     * 给定一个数组，将数组中的元素向右移动 k 个位置，其中 k 是非负数。
     * <p>
     *  
     * <p>
     * 进阶：
     * <p>
     * 尽可能想出更多的解决方案，至少有三种不同的方法可以解决这个问题。
     * 你可以使用空间复杂度为 O(1) 的 原地 算法解决这个问题吗？
     *  
     * <p>
     * 示例 1:
     * <p>
     * 输入: nums = [1,2,3,4,5,6,7], k = 3
     * 输出: [5,6,7,1,2,3,4]
     * 解释:
     * 向右旋转 1 步: [7,1,2,3,4,5,6]
     * 向右旋转 2 步: [6,7,1,2,3,4,5]
     * 向右旋转 3 步: [5,6,7,1,2,3,4]
     * 示例 2:
     * <p>
     * 输入：nums = [-1,-100,3,99], k = 2
     * 输出：[3,99,-1,-100]
     * 解释:
     * 向右旋转 1 步: [99,-1,-100,3]
     * 向右旋转 2 步: [3,99,-1,-100]
     * <p>
     * <p>
     * 来源：力扣（LeetCode）
     * 链接：https://leetcode-cn.com/problems/rotate-array
     * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
     */

    @Test
    public void main() {
        //力扣标签双指针，跟双指针有什么关系?黑人问号
        int[] nums = new int[]{1, 2};
        System.out.println(Arrays.toString(nums));
        rotate(nums, 4);
        System.out.println(Arrays.toString(nums));
    }

    /**
     * 执行结果：通过
     * 执行用时：0 ms, 在所有 Java 提交中击败了100.00%的用户
     * 内存消耗：55.3 MB, 在所有 Java 提交中击败了46.22%的用户
     */
    public void rotate(int[] nums, int k) {
        if (nums == null) return;
        if (k > nums.length) {
            rotate(nums, k - nums.length);
            return;
        }
        int[] newNums = nums.clone();
        System.arraycopy(newNums, nums.length - k, nums, 0, k);
        System.arraycopy(newNums, 0, nums, k, nums.length - k);
    }

}
