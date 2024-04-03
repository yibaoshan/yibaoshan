package com.android.algorithm.leetcode;

import org.junit.Test;

import java.util.Arrays;

public class Hard_72_编辑距离 {

    /**
     * 给你两个单词 word1 和 word2，请你计算出将 word1 转换成 word2 所使用的最少操作数 。
     * <p>
     * 你可以对一个单词进行如下三种操作：
     * <p>
     * 插入一个字符
     * 删除一个字符
     * 替换一个字符
     * <p>
     * <p>
     * 来源：力扣（LeetCode）
     * 链接：https://leetcode-cn.com/problems/edit-distance
     * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
     */

    @Test
    public void main() {
        String word1 = "horse";
        String word2 = "ros";
        System.out.println(minDistance(word1, word2));
    }

    /**
     * 动态规划
     * 执行结果：通过
     * 执行用时：5 ms, 在所有 Java 提交中击败了83.81%的用户
     * 内存消耗：38.6 MB, 在所有 Java 提交中击败了14.45%的用户
     */
    public int minDistance(String word1, String word2) {
        if (word1 == null || word2 == null) return 0;
        if (word1.equals(word2)) return 0;
        if (word1.length() < word2.length()) return 0;
        int[][] dp = new int[word1.length() + 1][word2.length() + 1];
        for (int i = 1; i <= word1.length(); i++) {
            dp[i][0] = i;
        }
        for (int j = 1; j <= word2.length(); j++) {
            dp[0][j] = j;
        }
        for (int i = 1; i <= word1.length(); i++) {
            char c1 = word1.charAt(i - 1);
            for (int j = 1; j <= word2.length(); j++) {
                char c2 = word2.charAt(j - 1);
                if (c1 == c2) {
                    dp[i][j] = dp[i - 1][j - 1];
                } else {
                    dp[i][j] = Math.min(Math.min(dp[i - 1][j - 1], dp[i - 1][j]), Math.min(dp[i - 1][j - 1], dp[i][j - 1])) + 1;
                }
                System.out.println(c1 + "," + c2 + " " + i + "," + j);
            }
            for (int j = 0; j < dp.length; j++) {
                System.out.println(Arrays.toString(dp[i]));
            }
            System.out.println();
        }
        return dp[word1.length()][word2.length()];
    }

    public int longestCommonSubsequence(String text1, String text2) {
        if (text1 == null || text2 == null) return 0;
        int[][] dp = new int[text1.length() + 1][text2.length() + 1];
        for (int i = 1; i <= text1.length(); i++) {
            for (int j = 1; j <= text2.length(); j++) {
                if (text1.charAt(i - 1) == text2.charAt(j - 1)) dp[i][j] = dp[i - 1][j - 1] + 1;
                else dp[i][j] = Math.max(dp[i - 1][j], dp[i][j - 1]);
            }
        }
        return dp[text1.length()][text2.length()];
    }

}
