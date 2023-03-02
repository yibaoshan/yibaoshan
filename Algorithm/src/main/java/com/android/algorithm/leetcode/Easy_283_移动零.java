package com.android.algorithm.leetcode;

import org.junit.Test;

import java.util.Arrays;

public class Easy_283_移动零 {

    /**
     * 给定一个数组 nums，编写一个函数将所有 0 移动到数组的末尾，同时保持非零元素的相对顺序。
     * <p>
     * 示例:
     * <p>
     * 输入: [0,1,0,3,12]
     * 输出: [1,3,12,0,0]
     * 说明:
     * <p>
     * 必须在原数组上操作，不能拷贝额外的数组。
     * 尽量减少操作次数。
     * <p>
     * 来源：力扣（LeetCode）
     * 链接：https://leetcode-cn.com/problems/move-zeroes
     * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
     */

    @Test
    public void main() {
        int[] nums = new int[]{0, 1};
        System.out.println(Arrays.toString(nums));
        moveZeroes(nums);
        System.out.println(Arrays.toString(nums));
    }

    /**
     * 执行结果：通过
     * 执行用时：1 ms, 在所有 Java 提交中击败了51.37%的用户
     * 内存消耗：39.8 MB, 在所有 Java 提交中击败了5.87的用户
     */
    public void moveZeroes(int[] nums) {
        //定义快慢指针，快指针正常走，遇到不为0的数赋值给慢指针，同时慢指针++
        //一遍循环过后不为0的都在数组前面，从慢指针后面的都可以赋值为0了
        int fast = 0, slow = 0;
        while (fast < nums.length) {
            if (nums[fast] != 0) {
                nums[slow] = nums[fast];
                slow++;
            }
            fast++;
        }
        while (slow < nums.length) {
            nums[slow] = 0;
            slow++;
        }
    }

    public void moveZeroes2(int[] nums) {
        // 思路，从第一个元素为 0 的开始，遇到非 0 交换
        // 时间复杂度度 O(n)
        if (nums == null || nums.length < 2) return;
        int left = 0, right = 0;
        while (left < nums.length && nums[left] != 0) left++;
        right = left + 1;
        while (right < nums.length) {
            if (nums[right] != 0) {
                nums[left++] = nums[right];
            }
            right++;
        }
        while (left < nums.length) nums[left++] = 0;
    }

    public void moveZeroes3(int[] nums) {
        if (nums == null || nums.length < 2) return;
        int[] tmp = new int[nums.length];
        int index = 0;
        for (int i = 0; i < nums.length; i++) {
            if (nums[i] != 0) tmp[index++] = nums[i];
        }
        for (int i = 0; i < nums.length; i++) {
            nums[i] = tmp[i];
        }

    }

}
