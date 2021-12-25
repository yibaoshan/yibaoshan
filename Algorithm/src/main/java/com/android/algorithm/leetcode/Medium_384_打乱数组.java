package com.android.algorithm.leetcode;

import org.junit.Test;

import java.util.Arrays;
import java.util.Random;

public class Medium_384_打乱数组 {

    /**
     * 给你一个整数数组 nums ，设计算法来打乱一个没有重复元素的数组。
     * <p>
     * 实现 Solution class:
     * <p>
     * Solution(int[] nums) 使用整数数组 nums 初始化对象
     * int[] reset() 重设数组到它的初始状态并返回
     * int[] shuffle() 返回数组随机打乱后的结果
     * <p>
     * 来源：力扣（LeetCode）
     * 链接：https://leetcode-cn.com/problems/shuffle-an-array
     * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
     */

    @Test
    public void main() {
        int[] nums = new int[]{1, 2, 3, 4};
        Solution solution = new Solution(nums);
        System.err.println(Arrays.toString(solution.shuffle()));
        System.out.println(Arrays.toString(solution.reset()));
        System.err.println(Arrays.toString(solution.shuffle()));
        System.out.println(Arrays.toString(solution.reset()));
        System.err.println(Arrays.toString(solution.shuffle()));
    }

    /**
     * 洗牌算法
     * 执行结果：通过
     * 执行用时：83 ms, 在所有 Java 提交中击败了64.76%的用户
     * 内存消耗：46.7 MB, 在所有 Java 提交中击败了34.68%的用户
     */
    class Solution {

        private int[] source;

        public Solution(int[] nums) {
            source = nums;
        }

        public int[] reset() {
            return source;
        }

        public int[] shuffle() {
            int[] clone = source.clone();
            Random random = new Random();
            for (int i = 0; i < clone.length; i++) {
                int tarInx = i + random.nextInt(clone.length - i);
                int temp = clone[i];
                clone[i] = clone[tarInx];
                clone[tarInx] = temp;
            }
            return clone;
        }
    }

}
