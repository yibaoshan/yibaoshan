package com.android.algorithm.leetcode;

import org.junit.Test;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.List;

public class Medium_47_全排列2 {

    /**
     * 给定一个可包含重复数字的序列 nums ，按任意顺序 返回所有不重复的全排列。
     */

    @Test
    public void main() {
        int[] nums = new int[]{3, 3, 0, 3};
//        int[] nums = new int[]{1, 1, 2};
        List<List<Integer>> res = permuteUnique(nums);
        for (int i = 0; i < res.size(); i++) {
            System.out.println(Arrays.toString(new List[]{res.get(i)}));
        }
    }

    /**
     * 回溯法，和46题全排列相比，多了一步剪枝
     * <p>
     * 执行结果：通过
     * 执行用时：2 ms, 在所有 Java 提交中击败了40.58%的用户
     * 内存消耗：39.3 MB, 在所有 Java 提交中击败了21.75%的用户
     */
    public List<List<Integer>> permuteUnique(int[] nums) {
        if (nums == null || nums.length == 0) return res;
        Arrays.sort(nums);
        boolean[] visited = new boolean[nums.length];
        backtrack(nums, visited);
        return res;
    }

    List<List<Integer>> res = new ArrayList<>();
    Deque<Integer> deque = new ArrayDeque<>();

    private void backtrack(int[] nums, boolean[] visited) {
        if (deque.size() == nums.length) {
            res.add(new ArrayList<>(deque));
            return;
        }
        for (int i = 0; i < nums.length; i++) {
            if (visited[i]) continue;
            if (i > 0 && nums[i] == nums[i - 1] && visited[i - 1]) continue;//如果自身和上一个相同，并且上一个已经访问过
            visited[i] = true;
            deque.addLast(nums[i]);
            backtrack(nums, visited);
            deque.removeLast();
            visited[i] = false;
        }
    }


}
