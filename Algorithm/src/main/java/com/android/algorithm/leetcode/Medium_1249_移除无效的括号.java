package com.android.algorithm.leetcode;

import org.junit.Test;

import java.util.Stack;

public class Medium_1249_移除无效的括号 {

    /**
     * 给你一个由 '('、')' 和小写字母组成的字符串 s。
     * <p>
     * 你需要从字符串中删除最少数目的 '(' 或者 ')' （可以删除任意位置的括号)，使得剩下的「括号字符串」有效。
     * <p>
     * 请返回任意一个合法字符串。
     * <p>
     * 有效「括号字符串」应当符合以下 任意一条 要求：
     * <p>
     * 空字符串或只包含小写字母的字符串
     * 可以被写作 AB（A 连接 B）的字符串，其中 A 和 B 都是有效「括号字符串」
     * 可以被写作 (A) 的字符串，其中 A 是一个有效的「括号字符串」
     * <p>
     * <p>
     * 来源：力扣（LeetCode）
     * 链接：https://leetcode-cn.com/problems/minimum-remove-to-make-valid-parentheses
     * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
     */

    @Test
    public void main() {
//        String str = "a)b(c)d";
//        String str = "lee(t(c)o)de)";
//        String str = "))((";
        String str = "(a(b(c)d)";
        System.out.println(minRemoveToMakeValid(str));
    }

    /**
     * 两个栈实现，思路：
     * 1. 创建左右两个栈，用于保存下标值
     * 2. 遍历字符串，遇到左括号（'('）在左栈中保存自个的下标值（等会看看有没有人配对，没人配对就根据这个下标值来删除括号）
     * 3. 遇到右括号首先检测左栈是否有值，两种情况：
     * 3.1 若左栈为空，说明这个右括号是单身的，记录下标值，等会删除他
     * 3.2 若左栈不为空，说明右括号是有对象的，将他对象从左栈中删除，放过他们
     * 4. 循环跑完剩下的左栈和右栈都是单身的，删除即可，这里笔者用的是空格代替，后续转字符串时再删除所有空格
     * 执行结果：通过
     * 执行用时：17 ms, 在所有 Java 提交中击败了69.06%的用户
     * 内存消耗：39.4 MB, 在所有 Java 提交中击败了29.23%的用户
     */
    public String minRemoveToMakeValid(String s) {
        Stack<Integer> leftStack = new Stack<>();//保存左括号所在的下标值
        Stack<Integer> rightStack = new Stack<>();//右括号下标值
        char[] chars = s.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            char c = chars[i];
            if (c == '(') leftStack.push(i);
            else if (c == ')') {
                if (leftStack.empty()) rightStack.push(i);
                else leftStack.pop();
            }
        }
        while (!leftStack.empty() || !rightStack.empty()) {
            int delIndex;
            if (leftStack.empty()) delIndex = rightStack.pop();
            else if (rightStack.empty()) delIndex = leftStack.pop();
            else delIndex = rightStack.pop();
            chars[delIndex] = ' ';
        }
        return new String(chars).replaceAll(" ", "");
    }

}
