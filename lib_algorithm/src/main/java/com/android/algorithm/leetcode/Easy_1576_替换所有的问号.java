package com.android.algorithm.leetcode;

import org.junit.Test;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Stack;

public class Easy_1576_替换所有的问号 {

    /**
     * 给你一个仅包含小写英文字母和 '?' 字符的字符串 s，请你将所有的 '?' 转换为若干小写字母，使最终的字符串不包含任何 连续重复 的字符。
     * <p>
     * 注意：你 不能 修改非 '?' 字符。
     * <p>
     * 题目测试用例保证 除 '?' 字符 之外，不存在连续重复的字符。
     * <p>
     * 在完成所有转换（可能无需转换）后返回最终的字符串。如果有多个解决方案，请返回其中任何一个。可以证明，在给定的约束条件下，答案总是存在的。
     * <p>
     * 来源：力扣（LeetCode）
     * 链接：https://leetcode-cn.com/problems/replace-all-s-to-avoid-consecutive-repeating-characters
     * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
     */

    @Test
    public void main() {
        String s = "?zs";
        System.out.println(modifyString(s));
    }

    /**
     * 执行结果：通过
     * 执行用时：1 ms, 在所有 Java 提交中击败了100.00%的用户
     * 内存消耗：38.6 MB, 在所有 Java 提交中击败了16.93%的用户
     */
    public String modifyString(String s) {
        if (s == null || s.length() == 0) return s;
        char[] chars = s.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            if (chars[i] == '?') {
                char once = 'a';
                if (i == 0 && i + 1 < chars.length && chars[i + 1] == 'a') {
                    once++;
                } else if (i == chars.length - 1 && i != 0 && chars[i - 1] == 'a') {
                    once++;
                } else if (i > 0 && i + 1 < chars.length) {
                    while (chars[i - 1] == once || chars[i + 1] == once) {
                        once++;
                    }
                }
                chars[i] = once;
            }
        }
        return String.valueOf(chars);
    }


}
