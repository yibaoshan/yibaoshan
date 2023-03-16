package com.android.algorithm.leetcode;

import org.junit.Test;

import java.util.HashMap;

public class Easy_167_两数之和2输入有序数组 {

    /**
     * 给定一个已按照 非递减顺序排列  的整数数组 numbers ，请你从数组中找出两个数满足相加之和等于目标数 target 。
     * <p>
     * 函数应该以长度为 2 的整数数组的形式返回这两个数的下标值。numbers 的下标 从 1 开始计数 ，所以答案数组应当满足 1 <= answer[0] < answer[1] <= numbers.length 。
     * <p>
     * 你可以假设每个输入 只对应唯一的答案 ，而且你 不可以 重复使用相同的元素。
     * <p>
     * 来源：力扣（LeetCode）
     * 链接：https://leetcode-cn.com/problems/two-sum-ii-input-array-is-sorted
     * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
     */

    @Test
    public void main() {

    }

    /**
     * hash法
     * 执行结果：通过
     * 执行用时：2 ms, 在所有 Java 提交中击败了35.54%的用户
     * 内存消耗：38.7 MB, 在所有 Java 提交中击败了41.46%的用户
     */
    public int[] twoSum(int[] numbers, int target) {
        //这道题和力扣第一题相比，增加了升序排列限定
        //但也可以直接套用hash
        //1. 遍历数组，将下标值和下标存如hash
        //2. 检查目标值和当前下标值之差有没有存在于hashmap中，存在直接返回下标即可
        HashMap<Integer, Integer> hashMap = new HashMap<>();
        for (int i = 0; i < numbers.length; i++) {
            if (hashMap.containsKey(target - numbers[i])) return new int[]{hashMap.get(target - numbers[i]), i + 1};
            hashMap.put(numbers[i], i + 1);
        }
        return null;
    }

    /**
     * 双指针解法
     * 执行结果：通过
     * 执行用时：0 ms, 在所有 Java 提交中击败了100.00% 的用户
     * 内存消耗：38.7 MB, 在所有 Java 提交中击败了46.70%的用户
     */
    public int[] twoSum2(int[] numbers, int target) {
        //双指针解法，利用升序数组的特性
        //1. 左指针指向数组的左边，右指针指向数组的最右边
        //2. 左指针和右指针值相加
        //3. 若大于目标值，右指针减去1往左移一位
        //4. 若小于目标值，左指针加上1往右移一位
        //5. 若相等，那太好了，直接返回左指针和右指针即可
        int left = 0, right = numbers.length - 1;
        while (left < right) {
            if (numbers[left] + numbers[right] > target) right--;
            else if (numbers[left] + numbers[right] < target) left++;
            else return new int[]{left + 1, right + 1};
        }
        return null;
    }

}
