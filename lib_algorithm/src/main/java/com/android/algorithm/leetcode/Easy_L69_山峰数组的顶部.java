package com.android.algorithm.leetcode;

import org.junit.Test;

public class Easy_L69_山峰数组的顶部 {

    /**
     * 符合下列属性的数组 arr 称为 山峰数组（山脉数组） ：
     * <p>
     * arr.length >= 3
     * 存在 i（0 < i < arr.length - 1）使得：
     * arr[0] < arr[1] < ... arr[i-1] < arr[i]
     * arr[i] > arr[i+1] > ... > arr[arr.length - 1]
     * 给定由整数组成的山峰数组 arr ，返回任何满足 arr[0] < arr[1] < ... arr[i - 1] < arr[i] > arr[i + 1] > ... > arr[arr.length - 1] 的下标 i ，即山峰顶部。
     * <p>
     * 示例 1：
     * <p>
     * 输入：arr = [0,1,0]
     * 输出：1
     * 示例 2：
     * <p>
     * 输入：arr = [1,3,5,4,2]
     * 输出：2
     * 示例 3：
     * <p>
     * 输入：arr = [0,10,5,2]
     * 输出：1
     * 示例 4：
     * <p>
     * 输入：arr = [3,4,5,1]
     * 输出：2
     * <p>
     * 来源：力扣（LeetCode）
     * 链接：https://leetcode-cn.com/problems/B1IidL
     * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
     */

    @Test
    public void main() {
        System.out.println(peakIndexInMountainArray(new int[]{0, 1, 0}));
    }

    /**
     * 执行结果：通过
     * 执行用时：1 ms, 在所有 Java 提交中击败了10.88%的用户
     * 内存消耗：38.8 MB, 在所有 Java 提交中击败了37.88%的用户
     */
    public int peakIndexInMountainArray(int[] arr) {
        if (arr == null) return -1;
        if (arr.length < 3) return -1;
        int maxValue = arr[0];
        int maxValueIndex = -1;
        boolean findMaxValue = false;
        for (int i = 1; i < arr.length; i++) {
            if (arr[i] > maxValue) {
                if (findMaxValue) return -1;
                maxValue = arr[i];
                maxValueIndex = i;
            } else if (!findMaxValue) {
                findMaxValue = true;
            }
        }
        return maxValueIndex;
    }

}
