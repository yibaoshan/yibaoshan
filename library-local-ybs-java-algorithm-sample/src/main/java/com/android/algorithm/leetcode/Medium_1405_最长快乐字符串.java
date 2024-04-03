package com.android.algorithm.leetcode;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class Medium_1405_最长快乐字符串 {

    @Test
    public void main() {
        int a = 4;
        int b = 4;
        int c = 3;
        System.out.println(longestDiverseString(a, b, c));
    }

    /**
     * 没做出来
     */
    public String longestDiverseString(int a, int b, int c) {
        Stack<Character> stackA = new Stack<>();
        Stack<Character> stackB = new Stack<>();
        Stack<Character> stackC = new Stack<>();
        for (int i = 0; i < a; i++) stackA.push('a');
        for (int i = 0; i < b; i++) stackB.push('b');
        for (int i = 0; i < c; i++) stackC.push('c');
        List<Stack<Character>> list = new ArrayList<>();
        list.add(stackA);
        list.add(stackB);
        list.add(stackC);
        list.sort((stack, t1) -> {
            if (stack.size() > t1.size()) return -1;
            else if (stack.size() < t1.size()) return 1;
            return 0;
        });
        StringBuilder sb = new StringBuilder();
        while (!stackA.isEmpty() || !stackB.isEmpty() || !stackC.isEmpty()) {
            if (!list.get(0).isEmpty()) {
                append(sb, list.get(0).pop());
            }
            if (!list.get(1).isEmpty()) {
                append(sb, list.get(1).pop());
            }
            if (!list.get(2).isEmpty()) {
                append(sb, list.get(2).pop());
            }
        }
        return sb.toString();
    }

    private void append(StringBuilder sb, char c) {
        int indexOf = sb.lastIndexOf(c + "" + c);
        if (indexOf >= 0) {
            boolean flag = false;
            for (int i = indexOf + 1; i < sb.length(); i++) {
                if (sb.charAt(i) != c) {
                    sb.insert(i + 1, c);
                    flag = true;
                    break;
                }
            }
            if (!flag && indexOf + 2 != sb.length()) sb.append(c);
        } else {
            int lastIndexOf = sb.lastIndexOf("" + c);
            if (lastIndexOf >= 0) sb.insert(lastIndexOf + 1, c);
            else sb.append(c);
        }
    }

}
