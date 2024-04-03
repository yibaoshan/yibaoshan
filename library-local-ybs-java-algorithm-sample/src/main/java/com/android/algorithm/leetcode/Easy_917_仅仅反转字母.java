package com.android.algorithm.leetcode;

import org.junit.Test;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.Stack;

public class Easy_917_仅仅反转字母 {

    @Test
    public void main() {
//        String s = "ab-cd";
        String s = "Test1ng-Leet=code-Q!";
        System.out.println(reverseOnlyLetters(s));
        System.out.println(reverseOnlyLetters(s).equals("Qedo1ct-eeLg=ntse-T!"));
        System.out.println("Qedo1ct-eeLg=ntse-T!");
    }

    /**
     * 执行结果：通过
     * 执行用时：2 ms, 在所有 Java 提交中击败了9.63%的用户
     * 内存消耗：39.5 MB, 在所有 Java 提交中击败了9.48%的用户
     */
    public String reverseOnlyLetters(String s) {
        if (s == null || s.length() == 0) return "";
        Queue<Integer> queue = new ArrayDeque<>();
        Queue<Character> characters = new ArrayDeque<>();
        Stack<Character> stack = new Stack<>();
        char[] chars = s.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            if (chars[i] >= 'a' && chars[i] <= 'z' || chars[i] >= 'A' && chars[i] <= 'Z') stack.push(chars[i]);
            else {
                queue.add(i);
                characters.add(chars[i]);
            }
        }
        for (int i = 0; i < chars.length; i++) {
            if (!queue.isEmpty() && queue.peek() == i) {
                chars[i] = characters.poll();
                queue.poll();
            } else chars[i] = stack.pop();
        }
        return new String(chars);
    }

}
