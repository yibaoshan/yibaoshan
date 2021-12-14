package com.android.algorithm.leetcode;

import org.junit.Test;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class Medium_78_子集 {

    /**
     * 给你一个整数数组 nums ，数组中的元素 互不相同 。返回该数组所有可能的子集（幂集）。
     * <p>
     * 解集 不能 包含重复的子集。你可以按 任意顺序 返回解集。
     */

    @Test
    public void main() {
        int[] nums = new int[]{2, 1, 3};
        List<List<Integer>> lists = subsets(nums);
        for (List<Integer> list : lists) System.out.println(Arrays.toString(new List[]{list}));
        System.out.println(lists.size());
    }

    /**
     * 执行结果：通过
     * 执行用时：1 ms, 在所有 Java 提交中击败了26.32%的用户
     * 内存消耗：38.2 MB, 在所有 Java 提交中击败了97.54%的用户
     */
    public List<List<Integer>> subsets(int[] nums) {
        List<List<Integer>> res = new LinkedList<>();
        res.add(new LinkedList<>());
        for (int i = 0; i < nums.length; i++) {
            int count = res.size();
            for (int j = 0; j < count; j++) {
                List<Integer> cur = new LinkedList<>(res.get(j));
                cur.add(nums[i]);
                res.add(cur);
            }
        }
        return res;
    }

    private void find(int[] nums, List<Integer> list, List<Integer> res) {
        for (int i = 0; i < nums.length; i++) {

        }
    }

}
