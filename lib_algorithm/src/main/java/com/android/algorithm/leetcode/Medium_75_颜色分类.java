package com.android.algorithm.leetcode;

import org.junit.Test;

import java.util.Arrays;

public class Medium_75_颜色分类 {

    /**
     * 给定一个包含红色、白色和蓝色，一共 n 个元素的数组，原地对它们进行排序，使得相同颜色的元素相邻，并按照红色、白色、蓝色顺序排列。
     * <p>
     * 此题中，我们使用整数 0、 1 和 2 分别表示红色、白色和蓝色。
     * <p>
     * 来源：力扣（LeetCode）
     * 链接：https://leetcode-cn.com/problems/sort-colors
     * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
     */

    @Test
    public void main() {
        int[] nums = new int[]{2, 0, 2, 1, 1, 0};
        System.out.println(Arrays.toString(nums));
        sortColors(nums);
        System.out.println(Arrays.toString(nums));
    }

    /** 双指针快排，时间复杂度0(n*log(n))
     * 执行结果：通过
     * 执行用时：1 ms, 在所有 Java 提交中击败了100.00%的用户
     * 内存消耗：36.6 MB, 在所有 Java 提交中击败了97.22%的用户
     */
    public void sortColors(int[] nums) {
        if (nums == null || nums.length == 1) return;
        quickSort(nums, 0, nums.length - 1);
    }

    private void quickSort(int[] array, int begin, int end) {
        if (begin >= end) return;
        int low = begin, high = end;
        int cur = array[low];
        boolean direction = true;
        out:
        while (low < high) {
            if (direction) {
                for (int i = high; i >= low; i--) {
                    if (array[i] < cur) {
                        array[low] = array[i];
                        low++;
                        high = i;
                        direction = !direction;
                        continue out;
                    }
                }
                high = low;
            } else {
                for (int i = low; i <= high; i++) {
                    if (array[i] > cur) {
                        array[high] = array[i];
                        high--;
                        low = i;
                        direction = !direction;
                        continue out;
                    }
                }
                low = high;
            }
        }
        array[low] = cur;
        quickSort(array, begin, low - 1);
        quickSort(array, low + 1, end);
    }

}
