package com.android.algorithm.leetcode;

import org.junit.Test;

import java.util.Stack;

public class Easy_844_比较含退格的字符串 {

    /**
     * 给定 s 和 t 两个字符串，当它们分别被输入到空白的文本编辑器后，请你判断二者是否相等。# 代表退格字符。
     * <p>
     * 如果相等，返回 true ；否则，返回 false 。
     * <p>
     * 注意：如果对空文本输入退格字符，文本继续为空。
     * <p>
     * 来源：力扣（LeetCode）
     * 链接：https://leetcode-cn.com/problems/backspace-string-compare
     * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
     */

    @Test
    public void main() {
        String s = "bxj##tw";
        String t = "bxo#j##tw";
        System.out.println(backspaceCompare2(s, t));
    }

    /**
     * 两个栈实现，思路：
     * 1. 创建两个栈，分别遍历两个字符串，遇到#符号时弹出上一个保存的字符，最后比较两个栈即可
     * 执行结果：通过
     * 执行用时：1 ms, 在所有 Java 提交中击败了62.68%的用户
     * 内存消耗：36.6 MB, 在所有 Java 提交中击败了52.69%的用户
     */
    public boolean backspaceCompare(String s, String t) {
        if (s == null || t == null) return false;
        return buildStack(s.toCharArray()).equals(buildStack(t.toCharArray()));
    }

    private Stack<Character> buildStack(char[] chars) {
        Stack<Character> stack = new Stack<>();
        for (char c : chars) {
            if (c == '#') {
                if (!stack.isEmpty()) stack.pop();
            } else stack.push(c);
        }
        return stack;
    }

    /**
     * 双指针解法，未通过，懒得写了
     * 原因在findDel方法，没有跳过全部#号
     * */
    public boolean backspaceCompare2(String s, String t) {
        if (s == null || t == null) return false;
        char[] sChars = s.toCharArray();
        char[] tChars = t.toCharArray();
        //两个字符串都倒序遍历
        int sIndex = sChars.length - 1, tIndex = tChars.length - 1;
        while (sIndex >= 0 || tIndex >= 0) {
            //若遇到#符号，跳过比较
            sIndex -= findDel(sChars, sIndex);
            tIndex -= findDel(tChars, tIndex);
            //有一个字符串率先完成遍历
            if (sIndex < 0 || tIndex < 0) break;
            if (sChars[sIndex--] != tChars[tIndex--]) return false;
        }
        if (sIndex >= 0) sIndex -= findDel(sChars, sIndex);
        if (tIndex >= 0) tIndex -= findDel(tChars, tIndex);
        return sIndex < 0 && tIndex < 0;
    }

    private int findDel(char[] chars, int start) {
        int count = 0;
        for (int i = start; i >= 0; i--) {
            if (chars[i] == '#') count++;
            else break;
        }
        return count * 2;
    }

}
