package com.android.algorithm.leetcode;

import org.junit.Test;

public class Medium_151_颠倒字符串中的单词 {




    @Test
    public void main() {
        String s = "a       good   example";
        System.out.println(reverseWords(s));
    }

    /**
     * 思路：
     * 1. 删除收尾空格
     * 2. 翻转整个字符串
     * 3. 遍历字符串，将空格隔开的单次再次反转
     * 执行结果：通过
     * 执行用时：8 ms, 在所有 Java 提交中击败了34.71%的用户
     * 内存消耗：41.3 MB, 在所有 Java 提交中击败了21.79%的用户
     */
    public String reverseWords(String s) {
        if (s == null) return s;
        s = reverse(trim(s));
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            StringBuilder stringBuilder1 = new StringBuilder();
            while (i < s.length() && s.charAt(i) != ' ') {
                stringBuilder1.append(s.charAt(i));
                i++;
            }
            if (stringBuilder1.length() > 0) stringBuilder.append(reverse(stringBuilder1.toString())).append(' ');
        }
        return trim(stringBuilder.toString());
    }

    private String trim(String s) {
        StringBuilder stringBuilder = new StringBuilder(s);
        int index = 0;
        while (index < stringBuilder.length()) {
            if (stringBuilder.charAt(index) != ' ') break;
            stringBuilder.delete(index, index + 1);
        }
        index = stringBuilder.length() - 1;
        while (index >= 0) {
            if (stringBuilder.charAt(index) != ' ') break;
            stringBuilder.delete(index, index + 1);
            index = stringBuilder.length() - 1;
        }
        return stringBuilder.toString();
    }

    private String reverse(String s) {
        char[] chars = s.toCharArray();
        int left = 0, right = s.length() - 1;
        while (left < right) {
            char c = chars[right];
            chars[right] = chars[left];
            chars[left] = c;
            left++;
            right--;
        }
        return new String(chars);
    }

}
