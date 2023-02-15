package com.android.algorithm.leetcode;

import org.junit.Test;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;

public class Medium_39_组合总和 {

    /**
     * 给你一个 无重复元素 的整数数组 candidates 和一个目标整数 target ，找出 candidates 中可以使数字和为目标数 target 的 所有不同组合 ，并以列表形式返回。你可以按 任意顺序 返回这些组合。
     * <p>
     * candidates 中的 同一个 数字可以 无限制重复被选取 。如果至少一个数字的被选数量不同，则两种组合是不同的。 
     * <p>
     * 对于给定的输入，保证和为 target 的不同组合数少于 150 个。
     * <p>
     * 来源：力扣（LeetCode）
     * 链接：https://leetcode-cn.com/problems/combination-sum
     * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
     */

    @Test
    public void main() {
        int[] nums = new int[]{2, 3, 5};
        int target = 8;
        List<List<Integer>> lists = combinationSum(nums, target);
        for (int i = 0; i < lists.size(); i++) {
            System.err.println(Arrays.toString(new List[]{lists.get(i)}));
        }
    }

    private int cnt;
    private HashSet<String> hashSet = new HashSet<>();
    private List<Integer> integers = new ArrayList<>();
    private List<List<Integer>> ret = new ArrayList<>();

    public List<List<Integer>> combinationSum(int[] candidates, int target) {
        backtrack(candidates, target);
        return ret;
    }

    private void backtrack(int[] candidates, int target) {
        if (cnt > target) return;
        if (cnt == target) {
            ArrayList<Integer> tmp = new ArrayList<>(this.integers);
            Collections.sort(tmp);
            if (hashSet.add(tmp.toString())) ret.add(tmp);
            return;
        }
        for (int i = 0; i < candidates.length; i++) {
            integers.add(candidates[i]);
            cnt += candidates[i];
            backtrack(candidates, target);
            cnt -= candidates[i];
            integers.remove(integers.size() - 1);
        }
    }


}
