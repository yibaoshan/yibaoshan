package com.android.algorithm.leetcode;

import org.junit.Test;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

public class Medium_797_所有可能的路径 {

    @Test
    public void main() {

    }

    /**
     * 深搜
     * 执行结果：通过
     * 执行用时：2 ms, 在所有 Java 提交中击败了83.32%的用户
     * 内存消耗：39.5 MB, 在所有 Java 提交中击败了98.65%的用户
     */
    public List<List<Integer>> allPathsSourceTarget(int[][] graph) {
        List<List<Integer>> ans = new ArrayList<List<Integer>>();
        Deque<Integer> stack = new ArrayDeque<Integer>();
        stack.offerLast(0);
        dfs(graph, 0, graph.length - 1, ans, stack);
        return ans;
    }

    public void dfs(int[][] graph, int x, int n, List<List<Integer>> ans, Deque<Integer> stack) {
        if (x == n) {
            ans.add(new ArrayList<>(stack));
            return;
        }
        for (int y : graph[x]) {
            stack.offerLast(y);
            dfs(graph, y, n, ans, stack);
            stack.pollLast();
        }
    }

}
