package com.android.algorithm.leetcode;

import org.junit.Test;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

public class Medium_77_组合 {

    /**
     * 给定两个整数 n 和 k，返回范围 [1, n] 中所有可能的 k 个数的组合。
     * <p>
     * 你可以按 任何顺序 返回答案。
     * <p>
     *  
     * <p>
     * 示例 1：
     * <p>
     * 输入：n = 4, k = 2
     * 输出：
     * [
     * [2,4],
     * [3,4],
     * [2,3],
     * [1,2],
     * [1,3],
     * [1,4],
     * ]
     * <p>
     * 来源：力扣（LeetCode）
     * 链接：https://leetcode-cn.com/problems/combinations
     * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
     */

    @Test
    public void main() {
        List<List<Integer>> result = combine(4, 2);
        for (int i = 0; i < result.size(); i++) {
            for (int j = 0; j < result.get(i).size(); j++) {
                System.out.print(result.get(i).get(j) + ",");
            }
            System.out.println();
        }
    }

    /**
     * 回溯法
     * 执行结果：通过
     * 执行用时：19 ms, 在所有 Java 提交中击败了17.58%的用户
     * 内存消耗：39.7 MB, 在所有 Java 提交中击败了56.93%的用户
     */
    public List<List<Integer>> combine(int n, int k) {
        Deque<Integer> deque = new LinkedList<>();
        List<List<Integer>> res = new ArrayList<>();
        dfs(n, k, 1, deque, res);
        return res;
    }

    private void dfs(int n, int k, int start, Deque<Integer> deque, List<List<Integer>> res) {
        if (deque.size() == k) {
            res.add(new ArrayList<>(deque));
            return;
        }
        for (int i = start; i <= n; i++) {
            deque.addLast(i);
            dfs(n, k, i + 1, deque, res);
            deque.removeLast();
        }
    }

}
