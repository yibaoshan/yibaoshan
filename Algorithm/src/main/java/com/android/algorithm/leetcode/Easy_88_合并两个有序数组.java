package com.android.algorithm.leetcode;

import org.junit.Test;

import java.util.Arrays;

public class Easy_88_合并两个有序数组 {

    /**
     * 给你两个按 非递减顺序 排列的整数数组 nums1 和 nums2，另有两个整数 m 和 n ，分别表示 nums1 和 nums2 中的元素数目。
     * <p>
     * 请你 合并 nums2 到 nums1 中，使合并后的数组同样按 非递减顺序 排列。
     * <p>
     * 注意：最终，合并后数组不应由函数返回，而是存储在数组 nums1 中。为了应对这种情况，nums1 的初始长度为 m + n，其中前 m 个元素表示应合并的元素，后 n 个元素为 0 ，应忽略。nums2 的长度为 n 。
     * <p>
     * <p>
     * 来源：力扣（LeetCode）
     * 链接：https://leetcode-cn.com/problems/merge-sorted-array
     * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
     */

    @Test
    public void main() {
        int[] nums1 = new int[]{1, 2, 3, 0, 0, 0};
        int m = 3;
        int[] nums2 = new int[]{2, 5, 6};
        int n = nums2.length;
        merge(nums1, m, nums2, n);
        System.out.println(Arrays.toString(nums1));
    }

    /**
     * 官方双指针解法
     * 执行结果：通过
     * 执行用时：0 ms, 在所有 Java 提交中击败了100.00%的用户
     * 内存消耗：38.4 MB, 在所有 Java 提交中击败了73.86%的用户
     */

    public void merge(int[] nums1, int m, int[] nums2, int n) {
        int index1 = 0, index2 = 0;
        int tempValue;
        int[] sortArray = new int[m + n];
        while (index1 < m || index2 < n) {
            if (index1 == m) {
                tempValue = nums2[index2++];
            } else if (index2 == n) {
                tempValue = nums1[index1++];
            } else if (nums1[index1] < nums2[index2]) {
                tempValue = nums1[index1++];
            } else {
                tempValue = nums2[index2++];
            }
            sortArray[index1 + index2 - 1] = tempValue;
        }
        for (int i = 0; i < sortArray.length; i++) {
            nums1[i] = sortArray[i];
        }
    }


}
