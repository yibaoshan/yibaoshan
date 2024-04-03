package com.android.algorithm.leetcode;

import org.junit.Test;

public class Medium_11_盛最多水的容器 {

    /**
     * 给你 n 个非负整数 a1，a2，...，an，每个数代表坐标中的一个点 (i, ai) 。在坐标内画 n 条垂直线，垂直线 i 的两个端点分别为 (i, ai) 和 (i, 0) 。找出其中的两条线，使得它们与 x 轴共同构成的容器可以容纳最多的水。
     * <p>
     * 说明：你不能倾斜容器。
     * <p>
     * 来源：力扣（LeetCode）
     * 链接：https://leetcode-cn.com/problems/container-with-most-water
     * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
     */

    @Test
    public void main() {
        int[] height = new int[]{1, 8, 6, 2, 5, 4, 8, 3, 7};
        System.out.println(maxArea(height));
    }

    /**
     * 双指针解法，思路：左右指针从最左侧和最右侧开始遍历，那边值小就动那边的指针，同时更新最大值即可
     * 执行结果：通过
     * 执行用时：5 ms, 在所有 Java 提交中击败了15.65%的用户
     * 内存消耗：52.2 MB, 在所有 Java 提交中击败了5.06%的用户
     */
    public int maxArea(int[] height) {
        if (height == null || height.length < 2) return 0;
        if (height.length == 2) return Math.min(height[0], height[1]);
        int left = 0, right = height.length - 1, length = right;
        int max = 0;
        while (left < right) {
            int leftValue = height[left];
            int rightValue = height[right];
            max = Math.max(max, Math.min(height[left], height[right]) * length);
            if (leftValue < rightValue) left++;
            else right--;
            length--;
        }
        return max;
    }

    public int maxArea2(int[] height) {
        if (height.length <= 1) return -1;
        int i = 0, j = height.length - 1, res = 0;
        while (i < j) {
            int h = Math.min(height[i], height[j]);
            res = Math.max(res, h * (j - i));
            if (height[i] < height[j]) ++i;
            else --j;
        }
        return res;
    }

}
