package com.android.algorithm.leetcode;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Medium_47_全排列2 {

    /**
     * 给定一个可包含重复数字的序列 nums ，按任意顺序 返回所有不重复的全排列。
     */

    @Test
    public void main() {
        int[] nums = new int[]{3, 3, 0, 3};
        List<List<Integer>> res = permuteUnique(nums);
        for (int i = 0; i < res.size(); i++) {
            System.out.println(Arrays.toString(new List[]{res.get(i)}));
        }
    }

    /**
     * 回溯法，和46题全排列相比，多了一步剪枝
     * <p>
     * 执行结果：通过
     * 执行用时：3 ms, 在所有 Java 提交中击败了23.27%的用户
     * 内存消耗：39.2 MB, 在所有 Java 提交中击败了35.17%的用户
     */
    public List<List<Integer>> permuteUnique(int[] nums) {
        List<List<Integer>> res = new ArrayList<>();
        if (nums == null) return res;
        Arrays.sort(nums);
        List<Integer> list = new ArrayList<>();
        boolean[] visited = new boolean[nums.length];
        dfs(nums, list, res, visited);
        return res;
    }

    private void dfs(int[] nums, List<Integer> list, List<List<Integer>> res, boolean[] visited) {
        if (list.size() == nums.length) {
            res.add(new ArrayList<>(list));
            return;
        }
        for (int i = 0; i < nums.length; i++) {
            if (visited[i]) continue;
            if (i > 0 && nums[i] == nums[i - 1] && visited[i - 1]) continue;
            list.add(nums[i]);
            visited[i] = true;
            dfs(nums, list, res, visited);
            list.remove(list.size() - 1);
            visited[i] = false;
        }
    }

}
