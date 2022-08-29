package com.android.algorithm.leetcode;

import org.junit.Test;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import javax.sound.midi.Soundbank;

public class Medium_139_单词拆分 {

    /**
     * 给你一个字符串 s 和一个字符串列表 wordDict 作为字典，判定 s 是否可以由空格拆分为一个或多个在字典中出现的单词。
     * <p>
     * 说明：拆分时可以重复使用字典中的单词。
     * <p>
     * 来源：力扣（LeetCode）
     * 链接：https://leetcode-cn.com/problems/word-break
     * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
     */

    @Test
    public void main() {
        String s = "leetcode";
        List<String> wordDict = new ArrayList<>();
        wordDict.add("leet");
        wordDict.add("code");
        Queue<Character> queue = new LinkedList<>();
        queue.
        System.out.println(wordBreak(s, wordDict));
        System.out.println(wordBreak2(s, wordDict));
    }

    public boolean wordBreak(String s, List<String> wordDict) {
        if (s == null || wordDict == null || wordDict.size() == 0) return false;
        // 可以类比于背包问题
        int n = s.length();
        // memo[i] 表示 s 中以 i - 1 结尾的字符串是否可被 wordDict 拆分
        boolean[] dp = new boolean[n + 1];
        dp[0] = true;
        for (int i = 1; i <= n; i++) {
            for (int j = 0; j < i; j++) {
                if (dp[j] && wordDict.contains(s.substring(j, i))) {
                    dp[i] = true;
                    break;
                }
            }
        }
        return dp[n];
    }

    //回溯法，超时
    public boolean wordBreak2(String s, List<String> wordDict) {
        backtrack(s, wordDict, 0);
        return result;
    }

    boolean result = false;
    StringBuilder sb = new StringBuilder();

    private void backtrack(String s, List<String> wordDict, int start) {
        if (result) return;
        if (sb.length() == s.length() && sb.toString().equals(s)) {
            result = true;
            return;
        }
        if (sb.length() >= s.length()) return;
        for (int i = start; i < wordDict.size(); i++) {
            sb.append(wordDict.get(i));
            backtrack(s, wordDict, start);
            sb.delete(sb.length() - wordDict.get(i).length(), sb.length());
        }
        return;
    }

}
