package com.android.algorithm.leetcode;

import org.junit.Test;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
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
        int[] nums = new int[]{2, 3, 6, 7};
        int target = 7;
        List<List<Integer>> lists = combinationSum(nums, target);
        for (int i = 0; i < lists.size(); i++) {
            System.out.println(Arrays.toString(new List[]{lists.get(i)}));
        }
    }

    /**
     * 回溯法
     * <p>
     * 执行结果：通过
     * 执行用时：64 ms, 在所有 Java 提交中击败了5.16%的用户
     * 内存消耗：39.1 MB, 在所有 Java 提交中击败了6.49%的用户
     */
    public List<List<Integer>> combinationSum(int[] candidates, int target) {
        Deque<Integer> path = new ArrayDeque<>();
        List<List<Integer>> res = new ArrayList<>();
        cnt = new int[candidates.length];
        dfs(path, res, candidates, target);
        return res;
    }

    private HashSet<String> hashSet = new HashSet<>();
    private int[] cnt;

    private void dfs(Deque<Integer> path, List<List<Integer>> res, int[] candidates, int target) {
        if (target == 0) {
            String str = Arrays.toString(cnt);
            if (!hashSet.contains(str)) {
                hashSet.add(str);
                res.add(new ArrayList<>(path));
            }
            return;
        }
        if (target < 0) {
            return;
        }
        for (int i = 0; i < candidates.length; i++) {
            int cur = candidates[i];
            path.addLast(cur);
            target -= cur;
            cnt[i] += 1;
            dfs(path, res, candidates, target);
            target += cur;
            cnt[i] -= 1;
            path.removeLast();
        }
    }


}
