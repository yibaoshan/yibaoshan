package com.android.algorithm.leetcode;

import org.junit.Test;

public class Easy_392_判断子序列 {

    @Test
    public void main() {
        String s = "abca";
        String t = "ahbgdc";
        System.out.println(isSubsequence(s, t));
    }

    public boolean isSubsequence(String s, String t) {
        int i = 0, j = 0;
        //题意说不能改变位置，那么用双指针从下标为0开始遍历，当i和j相同时，i++去找下一个字符，不相同是t向前后走一位j++
        while (i < s.length() && j < t.length()) {
            if (s.charAt(i) == t.charAt(j)) i++;
            j++;
        }
        return i == s.length();
    }

}
