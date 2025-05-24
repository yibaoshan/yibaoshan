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
     * 执行用时分布2ms击败97.94%
     * 消耗内存分布40.78MB击败95.89%
     */
    public boolean isValid(String s) {
        if (s == null || s.isEmpty() || s.length() % 2 != 0) return false;
        Stack<Character> stack = new Stack<>();
        char[] chars = s.toCharArray();
        for (char c : chars) {
            if (c == '(' || c == '[' || c == '{') stack.push(c);
            else if (stack.isEmpty() || c == ')' && stack.pop() != '('
                    || c == ']' && stack.pop() != '['
                    || c == '}' && stack.pop() != '{') return false;
        }
        return stack.isEmpty();
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
