package com.android.algorithm.leetcode;

import org.junit.Test;

public class Medium_583_两个字符串的删除操作 {

    /**
     * 给定两个单词 word1 和 word2，找到使得 word1 和 word2 相同所需的最小步数，每步可以删除任意一个字符串中的一个字符。
     */
    @Test
    public void main() {
        String word1 = "sea", word2 = "eat";
        System.out.println(minDistance(word1, word2));
    }

    /**
     * 动态规划
     * 执行结果：通过
     * 执行用时：7 ms, 在所有 Java 提交中击败了63.18%的用户
     * 内存消耗：39.2 MB, 在所有 Java 提交中击败了9.03%的用户
     */
    public int minDistance(String word1, String word2) {
        if (word1 == null || word2 == null) return 0;
        int[][] dp = new int[word1.length() + 1][word2.length() + 1];
        for (int i = 1; i <= word1.length(); i++) {
            for (int j = 1; j <= word2.length(); j++) {
                if (word1.charAt(i - 1) == word2.charAt(j - 1)) dp[i][j] = dp[i - 1][j - 1] + 1;
                else dp[i][j] = Math.max(dp[i - 1][j], dp[i][j - 1]);
            }
        }
        return word1.length() + word2.length() - 2 * dp[word1.length()][word2.length()];
    }

    /**
     * 未通过，题意理解错了
     */
    public int minDistance2(String word1, String word2) {
        if (word1 == null || word2 == null) return 0;
        int step = word1.length();
        if (word2.length() < step) step = word2.length();
        while (step > 0) {
            for (int i = 0; i < word1.length(); i++) {
                if (i + step == word1.length() + 1) break;
                String s1 = word1.substring(i, i + step);
                for (int j = 0; j < word2.length(); j++) {
                    if (j + step == word2.length() + 1) break;
                    String s2 = word2.substring(j, j + step);
                    if (s1.equals(s2)) return word1.length() + word2.length() - step * 2;
                }
            }
            step--;
        }
        return word1.length() + word2.length();
    }

}
