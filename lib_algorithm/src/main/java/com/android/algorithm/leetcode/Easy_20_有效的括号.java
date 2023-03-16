package com.android.algorithm.leetcode;

import org.junit.Test;

import java.util.Stack;

public class Easy_20_有效的括号 {

    /**
     * 给定一个只包括 '('，')'，'{'，'}'，'['，']' 的字符串 s ，判断字符串是否有效。
     * <p>
     * 有效字符串需满足：
     * <p>
     * 左括号必须用相同类型的右括号闭合。
     * 左括号必须以正确的顺序闭合。
     *  
     * <p>
     * 示例 1：
     * <p>
     * 输入：s = "()"
     * 输出：true
     * 示例 2：
     * <p>
     * 输入：s = "()[]{}"
     * 输出：true
     * 示例 3：
     * <p>
     * 输入：s = "(]"
     * 输出：false
     * 示例 4：
     * <p>
     * 输入：s = "([)]"
     * 输出：false
     * 示例 5：
     * <p>
     * 输入：s = "{[]}"
     * 输出：true
     * <p>
     * 来源：力扣（LeetCode）
     * 链接：https://leetcode-cn.com/problems/valid-parentheses
     * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
     */

    @Test
    public void main() {
        String s = "]";
        System.out.println(isValid(s));
    }

    /**
     * 执行结果：通过
     * 执行用时：1 ms, 在所有 Java 提交中击败了98.94%的用户
     * 内存消耗：36.5 MB, 在所有 Java 提交中击败了45.71%的用户
     */
    public boolean isValid(String s) {
        if (s == null) return false;
        char[] chars = s.toCharArray();
        int sBracket = 0, mBracket = 0, bBracket = 0;
        Stack<Integer> stack = new Stack<>();
        for (char c : chars) {
            if (c == '(') {
                sBracket++;
                stack.push(1);
            } else if (c == ')' && !stack.empty() && stack.peek() == 1) {
                sBracket--;
                stack.pop();
            } else if (c == '[') {
                mBracket++;
                stack.push(2);
            } else if (c == ']' && !stack.empty() && stack.peek() == 2) {
                mBracket--;
                stack.pop();
            } else if (c == '{') {
                bBracket++;
                stack.push(3);
            } else if (c == '}' && !stack.empty() && stack.peek() == 3) {
                bBracket--;
                stack.pop();
            } else return false;
        }
        return sBracket == 0 && mBracket == 0 && bBracket == 0;
    }

    /**
     * 评论区答案，虽然和上面的方法时间复杂度是一样的
     * 但很显然，下面这种实现方案更优雅更巧妙
     */
    public boolean isValid2(String s) {
        if (s == null) return false;
        char[] chars = s.toCharArray();
        Stack<Character> stack = new Stack<>();
        for (char c : chars) {
            if (c == '(') stack.push(')');
            else if (c == '[') stack.push(']');
            else if (c == '{') stack.push('}');
            else if (stack.empty() || stack.pop() != c) return false;
        }
        return true;
    }

}
