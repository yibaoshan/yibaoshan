package com.android.algorithm.leetcode;

import org.junit.Test;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Deque;
import java.util.List;

public class Medium_40_组合总和2 {

    /**
     * 给定一个数组 candidates 和一个目标数 target ，找出 candidates 中所有可以使数字和为 target 的组合。
     * <p>
     * candidates 中的每个数字在每个组合中只能使用一次。
     * <p>
     * 注意：解集不能包含重复的组合。 
     * <p>
     * 来源：力扣（LeetCode）
     * 链接：https://leetcode-cn.com/problems/combination-sum-ii
     * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
     */

    @Test
    public void main() {
//        int[] nums = new int[]{10, 1, 2, 7, 6, 1, 5};
        int[] nums = new int[]{1,1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1};
        System.out.println(nums.length);
        int target = 8;
        List<List<Integer>> lists = combinationSum2(nums, target);
        System.out.println(lists.size());
        for (int i = 0; i < lists.size(); i++) {
            System.out.println(Arrays.toString(new List[]{lists.get(i)}));
        }
    }

    /**
     * 执行结果：通过
     * 执行用时：2 ms, 在所有 Java 提交中击败了98.89%的用户
     * 内存消耗：38.5 MB, 在所有 Java 提交中击败了74.81%的用户
     */
    public List<List<Integer>> combinationSum2(int[] candidates, int target) {
        Arrays.sort(candidates);
        dfs(candidates, target, 0);
        return res;
    }

    List<List<Integer>> res = new ArrayList<>();
    Deque<Integer> path = new ArrayDeque<>();

    private void dfs(int[] candidates, int target, int start) {
        if (target == 0) {
            res.add(new ArrayList<>(path));
            return;
        }
        if (target < 0) return;
        for (int i = start; i < candidates.length; i++) {
            int cur = candidates[i];
            if (cur > target) continue;
            if (i > start && cur == candidates[i - 1]) continue;//重点在这
            path.addLast(cur);
            target -= cur;
            dfs(candidates, target, i + 1);
            target += cur;
            path.removeLast();
        }
    }


}
