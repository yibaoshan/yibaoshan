package com.android.algorithm.leetcode;

import org.junit.Test;

import java.util.Stack;

public class Easy_2000_反转单词前缀 {

    /**
     * 给你一个下标从 0 开始的字符串 word 和一个字符 ch 。找出 ch 第一次出现的下标 i ，反转 word 中从下标 0 开始、直到下标 i 结束（含下标 i ）的那段字符。如果 word 中不存在字符 ch ，则无需进行任何操作。
     * <p>
     * 例如，如果 word = "abcdefd" 且 ch = "d" ，那么你应该 反转 从下标 0 开始、直到下标 3 结束（含下标 3 ）。结果字符串将会是 "dcbaefd" 。
     * 返回 结果字符串 。
     * <p>
     * 来源：力扣（LeetCode）
     * 链接：https://leetcode-cn.com/problems/reverse-prefix-of-word
     * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
     */

    @Test
    public void main() {
        String word = "rzwuktxcjfpamlonbgyieqdvhs";
        char ch = 's';
        System.out.println("shvdqeiygbnolmapfjcxtkuwzr");
        System.out.println(reversePrefix(word, ch));
    }

    /**
     * 执行结果：通过
     * 执行用时：1 ms, 在所有 Java 提交中击败了25.99%的用户
     * 内存消耗：39.4 MB, 在所有 Java 提交中击败了29.60%的用户
     */
    public String reversePrefix(String word, char ch) {
        if (word == null || word.length() == 0) return word;
        Stack<Character> stack = new Stack<>();
        boolean flag = false;
        for (int i = 0; i < word.length(); i++) {
            stack.add(word.charAt(i));
            if (ch == word.charAt(i)) {
                flag = true;
                break;
            }
        }
        if (flag) {
            StringBuilder sb = new StringBuilder();
            while (!stack.isEmpty()) {
                sb.append(stack.pop());
            }
            sb.append(word.substring(sb.length()));
            return sb.toString();
        }
        return word;
    }

}
