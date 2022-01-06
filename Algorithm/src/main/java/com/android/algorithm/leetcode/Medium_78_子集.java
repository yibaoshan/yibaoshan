package com.android.algorithm.leetcode;

import org.junit.Test;

import java.util.Arrays;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

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

        lists = subsets2(nums);
        for (List<Integer> list : lists) System.out.println(Arrays.toString(new List[]{list}));
        System.out.println(lists.size());
    }

    /**
     * 暴力解法
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

    /**
     * 回溯解法
     * 执行结果：通过
     * 执行用时：1 ms, 在所有 Java 提交中击败了25.18%的用户
     * 内存消耗：38.9 MB, 在所有 Java 提交中击败了5.30%的用户
     */
    public List<List<Integer>> subsets2(int[] nums) {
        dfs(nums, 0);
        return res;
    }

    List<List<Integer>> res = new LinkedList<>();
    Deque<Integer> path = new LinkedList<>();

    private void dfs(int[] nums, int start) {
        if (path.size() > nums.length) return;
        res.add(new LinkedList<>(path));
        for (int i = start; i < nums.length; i++) {
            path.addLast(nums[i]);
            dfs(nums, i + 1);
            path.removeLast();
        }
    }

}
