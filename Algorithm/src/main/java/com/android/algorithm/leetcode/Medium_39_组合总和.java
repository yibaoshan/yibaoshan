package com.android.algorithm.leetcode;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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
        List<Integer> list = new ArrayList<>();
        List<List<Integer>> res = new ArrayList<>();
        find(candidates, target, 0, res, list);
        return res;
    }

    private void find(int[] nums, int target, int sum, List<List<Integer>> res, List<Integer> list) {
        if (sum == target) {
            List<Integer> once = new ArrayList<>(list);
            Collections.sort(once);
            for (int i = 0; i < res.size(); i++) {
                if (once.equals(res.get(i))) return;
            }
            res.add(once);
            return;
        }
        if (sum > target) return;
        for (int i = 0; i < nums.length; i++) {
            sum += nums[i];
            list.add(nums[i]);
            System.err.println("before:" + list.size());
            find(nums, target, sum, res, list);
            System.err.println("after:" + list.size());
            sum -= nums[i];
            list.remove(list.size() - 1);
        }
    }

}
