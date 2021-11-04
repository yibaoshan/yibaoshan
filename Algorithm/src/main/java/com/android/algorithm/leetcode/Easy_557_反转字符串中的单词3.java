package com.android.algorithm.leetcode;

import org.junit.Test;

public class Easy_557_反转字符串中的单词3 {

    /**
     * 给定一个字符串，你需要反转字符串中每个单词的字符顺序，同时仍保留空格和单词的初始顺序。
     * <p>
     * <p>
     * 示例：
     * <p>
     * 输入："Let's take LeetCode contest"
     * 输出："s'teL ekat edoCteeL tsetnoc"
     *  
     * <p>
     * 提示：
     * <p>
     * 在字符串中，每个单词由单个空格分隔，并且字符串中不会有任何额外的空格。
     * <p>
     * 来源：力扣（LeetCode）
     * 链接：https://leetcode-cn.com/problems/reverse-words-in-a-string-iii
     * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
     */

    @Test
    public void main() {
        String s = "Let's take LeetCode contest";
        System.out.println(reverseWords(s));
    }

    /**
     * 双指针解法
     * 执行结果：通过
     * 执行用时：5 ms, 在所有 Java 提交中击败了68.32%的用户
     * 内存消耗：39.1 MB, 在所有 Java 提交中击败了29.01%的用户
     */
    public String reverseWords(String s) {
        String[] strings = s.split(" ");
        StringBuilder stringBuilder = new StringBuilder();
        for (String str : strings) {
            stringBuilder.append(reverseString(str.toCharArray()));
            stringBuilder.append(" ");
        }
        stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        return stringBuilder.toString();
    }

    public char[] reverseString(char[] s) {
        int left = 0, right = s.length - 1;
        char c;
        while (left < s.length / 2) {
            c = s[right];
            s[right] = s[left];
            s[left] = c;
            left++;
            right--;
        }
        return s;
    }

}
