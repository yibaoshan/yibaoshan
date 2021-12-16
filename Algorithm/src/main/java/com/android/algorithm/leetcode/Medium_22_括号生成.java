package com.android.algorithm.leetcode;

import org.junit.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class Medium_22_括号生成 {

    /**
     * 数字 n 代表生成括号的对数，请你设计一个函数，用于能够生成所有可能的并且 有效的 括号组合。
     * <p>
     * 示例 1：
     * <p>
     * 输入：n = 3
     * 输出：["((()))","(()())","(())()","()(())","()()()"]
     * <p>
     * 来源：力扣（LeetCode）
     * 链接：https://leetcode-cn.com/problems/generate-parentheses
     * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
     */

    @Test
    public void main() {
        int n = 6;
        List<String> strings = generateParenthesis2(n);
        for (String str : strings) System.out.println(str);
    }

    private int left = 0, right = 0;
    private StringBuilder sb = new StringBuilder();
    private HashSet<String> hashSet = new HashSet<>();

    /**
     * 回溯暴力遍历所有可能性，超时
     */
    public List<String> generateParenthesis(int n) {
        List<String> res = new ArrayList<>();
        if (n < 1) return res;
        List<Character> pars = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            pars.add('(');
            pars.add(')');
        }
        boolean[] visited = new boolean[pars.size()];
        dfs(pars, pars.size(), visited);
        res.addAll(hashSet);
        return res;
    }

    private void dfs(List<Character> list, int n, boolean[] visited) {
        if (sb.length() == n) {
            hashSet.add(sb.toString());
            return;
        }
        for (int i = 0; i < list.size(); i++) {
            if (visited[i]) continue;
            if (right > left || left > n / 2 || right > n / 2) return;
            char c = list.get(i);
            if (c == '(') left++;
            else right++;
            sb.append(c);
            visited[i] = true;
            dfs(list, n, visited);
            sb.delete(sb.length() - 1, sb.length());
            visited[i] = false;
            if (c == '(') left--;
            else right--;
        }
    }

    List<String> res = new ArrayList<>();

    /**
     * 回溯法，评论区答案
     * 执行结果：通过
     * 执行用时：1 ms, 在所有 Java 提交中击败了74.11%的用户
     * 内存消耗：38.6 MB, 在所有 Java 提交中击败了39.63%的用户
     */
    public List<String> generateParenthesis2(int n) {
        dfs(n, n, "");
        return res;
    }

    private void dfs(int left, int right, String curStr) {
        if (left == 0 && right == 0) { // 左右括号都不剩余了，递归终止
            res.add(curStr);
            return;
        }
        if (left > 0) { // 如果左括号还剩余的话，可以拼接左括号
            dfs(left - 1, right, curStr + "(");
        }
        if (right > left) { // 如果右括号剩余多于左括号剩余的话，可以拼接右括号
            dfs(left, right - 1, curStr + ")");
        }
    }

}
