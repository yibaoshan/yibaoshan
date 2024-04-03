package com.android.algorithm.leetcode;

import org.junit.Test;

import java.util.Arrays;

public class Easy_541_反转字符串2 {

    /**
     * 给定一个字符串 s 和一个整数 k，从字符串开头算起，每计数至 2k 个字符，就反转这 2k 字符中的前 k 个字符。
     * <p>
     * 如果剩余字符少于 k 个，则将剩余字符全部反转。
     * 如果剩余字符小于 2k 但大于或等于 k 个，则反转前 k 个字符，其余字符保持原样。
     * <p>
     * 来源：力扣（LeetCode）
     * 链接：https://leetcode.cn/problems/reverse-string-ii/description/
     * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
     */

    @Test
    public void main() {
        // 0,4 , 4/2 = 2 ,4,8, 8/2
        String s = "abcdefgh";
        int k = 4;
        System.out.println(reverseStr(s, k));
    }

    public String reverseStr(String s, int k) {
        /**
         *
         * 刚开始以为是，在反转字符串的基础上，增加了每间隔 K 反转一次
         * 提交才发现，题目理解错了。。。
         *
         * 简单改了改遍历条件，通过了
         * */
        if (s == null || s.length() == 0) return s;
        int n = s.length();
        char[] chars = s.toCharArray();
        for (int i = 0; i < n; i += 2 * k) {
            if (i + k <= n) {
                reverse(chars, i, i + k - 1);
                continue;
            }
            reverse(chars, i, n - 1);
        }
        return new String(chars);
    }

    private void reverse(char[] chars, int left, int right) {
        while (left < right) {
            char c = chars[left];
            chars[left++] = chars[right];
            chars[right--] = c;
        }
    }

}
