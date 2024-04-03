package com.android.algorithm.leetcode;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Easy_459_重复的子字符串 {

    /**
     * 给定一个非空的字符串 s ，检查是否可以通过由它的一个子串重复多次构成。
     * <p>
     * <p>
     * 示例 1:
     * <p>
     * 输入: s = "abab"
     * 输出: true
     * 解释: 可由子串 "ab" 重复两次构成。
     * 示例 2:
     * <p>
     * 输入: s = "aba"
     * 输出: false
     * 示例 3:
     * <p>
     * 输入: s = "abcabcabcabc"
     * 输出: true
     * 解释: 可由子串 "abc" 重复四次构成。 (或子串 "abcabc" 重复两次构成。)
     * <p>
     * 来源：力扣（LeetCode）
     * 链接：https://leetcode.cn/problems/repeated-substring-pattern/
     * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
     */

    @Test
    public void main() {
        String s = "abcabc";
        System.out.println(repeatedSubstringPattern(s));
    }

    /**
     * S = N * s
     * <p>
     * S + S = 2N * s
     */
    public boolean repeatedSubstringPattern(String s) {
        /**
         * 这竟然是简单题。。错了2次
         *
         * 第一次，利用 split 函数，如果字符串能被完美分割，表示可以由子串重复组成
         * 提交超时，gg
         *
         * 第二次，暴力嵌套循环。 提交超时，gg
         *
         * 第三次，参考评论区偏移做法
         *
         * 假设 s = "abab" 那么 newstr = "abababab"
         * 掐头去尾后 newstr = "a[b(abab)a]b"，偏移后的新字符串依旧包含原来的 s ，表示能由重复子串组成 true
         *
         * 假设 s = "aba" 那么 newstr = "abaaba"
         * 掐头去尾后 newstr = "a[baab]a" ，新字符串不包含原先的 s，不满足条件，false
         *
         * */
        String newstr = s + s;
        return newstr.substring(1, newstr.length() - 1).contains(s);
    }

}
