package com.android.algorithm.leetcode;

import org.junit.Test;

import java.util.Arrays;

public class Medium_34_在排序数组中查找元素的第一个和最后一个位置 {

    /**
     * 给定一个按照升序排列的整数数组 nums，和一个目标值 target。找出给定目标值在数组中的开始位置和结束位置。
     * <p>
     * 如果数组中不存在目标值 target，返回 [-1, -1]。
     * <p>
     * 进阶：
     * <p>
     * 你可以设计并实现时间复杂度为 O(log n) 的算法解决此问题吗？
     * <p>
     * 来源：力扣（LeetCode）
     * 链接：https://leetcode-cn.com/problems/find-first-and-last-position-of-element-in-sorted-array
     * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
     */

    @Test
    public void main() {
        int[] nums = new int[]{5, 5};
        int target = 5;
        System.out.println(Arrays.toString(searchRange(nums, target)));
    }

    /**
     * 二分查找，思路：
     * 1. 通过二分查找找到目标值所在的下标
     * 2. 根据目标值所在下标开始左右遍历，找到第一个不为目标值的下标退出
     * 执行结果：通过
     * 执行用时：0 ms, 在所有 Java 提交中击败了100.00%的用户
     * 内存消耗：41.7 MB, 在所有 Java 提交中击败了42.99%的用户
     */
    public int[] searchRange(int[] nums, int target) {
        int[] res = new int[]{-1, -1};
        if (nums == null) return res;
        int left = 0, right = nums.length - 1;//左右指针分别指向起始位置：0和结束位置：数组的长度-1
        while (left <= right) {
            int midIndex = (left + right) / 2;
            int midVal = nums[midIndex];//得到左右指针中间元素
            if (target < midVal) {//若目标值小于中间值，说明中间值及其右边统统大于目标值，那么就需要将右边界收缩到数组长度的一半
                right = --midIndex;
            } else if (target > midVal) {//若目标值大于中间值，说明中间值及其左边都小于目标值，中间值左边的都不要了
                left = ++midIndex;
            } else {//找到目标值，由于题目要求找到数组中所有目标值并返回起始坐标，那么我们还需要从中间下标向左右两边扩散，找到第一个不等于下标值即可
                left = midIndex;
                right = midIndex;
                while (left > -1) {
                    if (nums[left] != target) break;//找到不等于目标值的下标，不向下执行了
                    res[0] = left;
                    left--;
                }
                while (right < nums.length) {
                    if (nums[right] != target) break;//找到右边界
                    res[1] = right;
                    right++;
                }
                break;
            }
        }
        return res;
    }

}
