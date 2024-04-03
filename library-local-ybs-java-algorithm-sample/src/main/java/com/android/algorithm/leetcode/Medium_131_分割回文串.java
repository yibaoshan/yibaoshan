package com.android.algorithm.leetcode;

import org.junit.Test;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.List;

public class Medium_131_分割回文串 {

    /**
     * 给你一个字符串 s，请你将 s 分割成一些子串，使每个子串都是 回文串 。返回 s 所有可能的分割方案。
     * <p>
     * 回文串 是正着读和反着读都一样的字符串。
     */

    @Test
    public void main() {
        String s = "aab";
        List<List<String>> res = partition(s);
        for (int i = 0; i < res.size(); i++) {
            System.out.println(Arrays.toString(new List[]{res.get(i)}));
        }
    }

    private List<List<String>> res = new ArrayList<>();
    private Deque<String> deque = new ArrayDeque<>();

    public List<List<String>> partition(String s) {
        dfs(s, 0);
        return res;
    }

    private void dfs(String s, int start) {
        if (start >= s.length()) {
            res.add(new ArrayList<>(deque));
            return;
        }
        for (int i = start; i < s.length(); i++) {
            String substring = s.substring(start, i + 1);
            if (isPalindrome(substring)) {
                deque.addLast(substring);
            } else continue;
            dfs(s, i + 1);
            deque.removeLast();
        }
    }

    private boolean isPalindrome(String s) {
        if (s == null || s.length() == 0) return false;
        if (s.length() == 1) return true;
        for (int i = 0; i < s.length(); i++) {
            if (i > s.length() - 1 - i) return true;
            if (s.charAt(i) != s.charAt(s.length() - 1 - i)) return false;
        }
        return true;
    }

}
