package com.android.algorithm.leetcode;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Medium_28_找出字符串中第一个匹配项的下标 {

    /**
     * 给你两个字符串 haystack 和 needle ，请你在 haystack 字符串中找出 needle 字符串的第一个匹配项的下标（下标从 0 开始）。
     * <p>
     * 如果 needle 不是 haystack 的一部分，则返回  -1 。
     * <p>
     * <p>
     * <p>
     * 示例 1：
     * <p>
     * 输入：haystack = "sadbutsad", needle = "sad"
     * 输出：0
     * 解释："sad" 在下标 0 和 6 处匹配。
     * 第一个匹配项的下标是 0 ，所以返回 0 。
     * 示例 2：
     * <p>
     * 输入：haystack = "leetcode", needle = "leeto"
     * 输出：-1
     * 解释："leeto" 没有在 "leetcode" 中出现，所以返回 -1 。
     */

    @Test
    public void main() {
        String haystack = "a";
        String needle = "a";
        System.out.println(strStr(haystack, needle));
    }

    public int strStr(String haystack, String needle) {
        // 简单题，纯模拟，暴力循环
        for (int i = 0; i <= haystack.length() - needle.length(); i++) {
            int index = -1, ret = -1;
            while (index++ < needle.length() - 1) {
                if (haystack.charAt(index + i) != needle.charAt(index)) {
                    ret++;
                    break;
                }
            }
            if (ret < 0) return i;
        }
        return -1;
    }

}
