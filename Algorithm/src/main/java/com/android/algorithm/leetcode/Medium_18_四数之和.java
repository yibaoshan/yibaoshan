package com.android.algorithm.leetcode;

import org.junit.Test;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Medium_18_四数之和 {

    @Test
    public void main() {
        int[] nums = new int[]{1000000000, 1000000000, 1000000000, 1000000000};
        int target = -294967296;
        List<List<Integer>> lists = fourSum(nums, target);
        for (int i = 0; i < lists.size(); i++) {
            System.out.println(lists.get(i).toString());
        }
    }

    public List<List<Integer>> fourSum(int[] nums, int target) {
        /**
         *
         * 和昨天的《三数之和》套路相同，回溯法和暴力遍历也都会超时
         *
         * ps：注意大数，测试用例有超过 int 表示范围的值，我图省事直接用long了
         *
         * 双指针解法，思路：
         *
         * 1. 把数组排序
         * 2. 双重遍历数组，在循环体中声明左右指针，分别指向以 j 为起始点的，数组左右两边
         * 2.1 判断 i、j、以及左右指针的四数之和，大于0，右指针左移，小于0，左指针右移
         * 2.2 直到找到等于 0，那么 左指针+1 右指针-1 继续循环。找到所有符合条件的下标
         *
         * 时间复杂度 O(n^3 + logn)
         */
        List<List<Integer>> ret = new ArrayList<>();
        Arrays.sort(nums); // 先排序
        for (int i = 0; i < nums.length; i++) {
            if (i > 0 && nums[i - 1] == nums[i]) continue; // 和上一个值相同，跳过重复值
            for (int j = i + 1; j < nums.length; j++) {
                if (j > i + 1 && nums[j - 1] == nums[j]) continue; // 同样跳过重复值
                int left = j + 1, right = nums.length - 1; // 双指针，左右指针指向当前 j 的左右两边
                while (right > left) {
                    long sum = nums[i] + nums[j] + nums[left] + nums[right];
                    if (sum > target) right--; // 四数之和太大了，右指针左移，去找更小的值
                    else if (sum < target) left++; // 太小了，左指针右移，去找更大的值
                    else {
                        // 符合目标值，结果保存到集合
                        ret.add(Arrays.asList(nums[i], nums[j], nums[left], nums[right]));
                        // 判断左右指针的下一个值，是否和其自身相同，注意检查下标边界
                        while (right > left && nums[right] == nums[right - 1]) right--;
                        while (right > left && nums[left] == nums[left + 1]) left++;
                        // 继续找下一组符合条件的下标
                        left++;
                        right--;
                    }
                }
            }
        }
        return ret;
    }

}
