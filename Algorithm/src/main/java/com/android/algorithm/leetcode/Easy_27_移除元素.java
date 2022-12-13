package com.android.algorithm.leetcode;

import org.junit.Test;

import java.util.Arrays;

public class Easy_27_移除元素 {

    @Test
    public void main() {
        int[] nums = new int[]{3, 2, 2, 3};
        int[] nums2 = new int[]{3, 2, 2, 3};
        int val = 3;
        System.out.println(removeElement(nums, val));

        System.out.println(removeElement2(nums2, val));

        System.out.println(Arrays.toString(nums));
        System.out.println(Arrays.toString(nums2));
    }

    public int removeElement2(int[] nums, int val) {
        /**
         * 思路，双指针，定义左右指针分别指向，数组左右两边
         *
         * 左指针负责从左到右推进，目的是找到和 val 相同的值，找到后交换
         * 右指针负责：当左指针拿到 val 相同值以后，保证自己指向的值不和 val 相同
         *
         * 以 nums = 3,2,2,3 ，val =3 来举例，left 默认为0 ，right 默认为 3
         *
         * 第一轮
         *
         * nums[left] == val 成立，准备和 right 发生交换
         * right 发现自己指向的值也是 3，和 val 相同，于是 right-- = 2，满足 nums[right] != val，发生交换
         *
         * 后面以此类推
         *
         * 空间复杂度 O(1) 时间复杂度 O(n)
         *
         * */
        int left = 0, right = nums.length - 1, cnt = 0;
        while (left <= right) {
            if (nums[left] == val) {
                for (int i = right; i > left; i--) { // 找到满足 nums[right] != val 的下标，并交换
                    if (nums[i] == val) {
                        cnt++;
                        continue;
                    }
                    swap(nums, left, i); // 交换，为了跑分高一点用了位运算
                    break;
                }
                cnt++;
            }
            left++;
            right = nums.length - 1 - cnt; // 实时更新右指针，如果后面都已经是被换下来的值，那就没必须要跑下去了
        }
        return nums.length - cnt;
    }

    private void swap(int[] nums, int i1, int i2) {
        nums[i1] = nums[i1] ^ nums[i2];
        nums[i2] = nums[i1] ^ nums[i2];
        nums[i1] = nums[i1] ^ nums[i2];
    }

    /**
     * 暴力循环
     * 执行结果：通过
     * 执行用时：0 ms, 在所有 Java 提交中击败了100.00%的用户
     * 内存消耗：40 MB, 在所有 Java 提交中击败了14.59%的用户
     */
    public int removeElement(int[] nums, int val) {
        int cnt = 0;
        for (int i = 0; i < nums.length - cnt; i++) {
            if (nums[i] == val) {
                for (int j = i; j < nums.length - 1 - cnt; j++) {
                    nums[j] = nums[j + 1];
                }
                i--;
                cnt++;
            }
        }
        return nums.length - cnt;
    }

}
