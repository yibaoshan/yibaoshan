package com.android.algorithm.leetcode;

import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

public class Easy_1763_最长的美好子字符串 {

    /**
     * 当一个字符串 s 包含的每一种字母的大写和小写形式 同时 出现在 s 中，就称这个字符串 s 是 美好 字符串。比方说，"abABB" 是美好字符串，因为 'A' 和 'a' 同时出现了，且 'B' 和 'b' 也同时出现了。然而，"abA" 不是美好字符串因为 'b' 出现了，而 'B' 没有出现。
     * <p>
     * 给你一个字符串 s ，请你返回 s 最长的 美好子字符串 。如果有多个答案，请你返回 最早 出现的一个。如果不存在美好子字符串，请你返回一个空字符串。
     * <p>
     * 来源：力扣（LeetCode）
     * 链接：https://leetcode-cn.com/problems/longest-nice-substring
     * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
     */

    @Test
    public void main() {
//        String s = "YazaAay";
//        String s = "Bb";
//        String s = "c";
//        String s = "dDzeE";
//        String s = "jcj";
        String s = "cChH";
        System.out.println(longestNiceSubstring(s));
    }

    /**
     * 执行结果：通过
     * 执行用时：113 ms, 在所有 Java 提交中击败了5.07%的用户
     * 内存消耗：41.4 MB, 在所有 Java 提交中击败了26.47%的用户
     */
    public String longestNiceSubstring(String s) {
        if (s == null || s.length() == 0) return "";
        int n = s.length();
        while (n > 1) {
            int start = 0;
            while (start + n <= s.length()) {
                String substring = s.substring(start, start + n);
                if (check(substring)) return substring;
                start++;
            }
            n--;
        }
        return "";
    }

    private boolean check(String s) {
        HashSet<Character> hashSet1 = new HashSet<>();
        HashSet<Character> hashSet2 = new HashSet<>();
        for (int i = 0; i < s.length(); i++) hashSet1.add(s.charAt(i));
        s = s.toUpperCase();
        for (int i = 0; i < s.length(); i++) hashSet2.add(s.charAt(i));
        return hashSet2.size() * 2 == hashSet1.size();
    }

}
