package com.android.algorithm.leetcode;

import org.junit.Test;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

public class Medium_784_字母大小写全排列 {

    /**
     * 给定一个字符串S，通过将字符串S中的每个字母转变大小写，我们可以获得一个新的字符串。返回所有可能得到的字符串集合。
     * <p>
     *  
     * <p>
     * 示例：
     * 输入：S = "a1b2"
     * 输出：["a1b2", "a1B2", "A1b2", "A1B2"]
     * <p>
     * 来源：力扣（LeetCode）
     * 链接：https://leetcode-cn.com/problems/letter-case-permutation
     * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
     */

    @Test
    public void main() {
        String s = "a1b2";
        List<String> strings = letterCasePermutation(s);
        for (int i = 0; i < strings.size(); i++) {
            System.out.println(strings.get(i));
        }
    }

    /**
     * 回溯法，评论区答案
     * 执行结果：通过
     * 执行用时：2 ms, 在所有 Java 提交中击败了69.30%的用户
     * 内存消耗：38.8 MB, 在所有 Java 提交中击败了96.99%的用户
     */
    public List<String> letterCasePermutation(String s) {
        List<String> result = new ArrayList<>();
        backtrack(result, 0, s, new StringBuilder());
        return result;
    }

    public void backtrack(List<String> result, int index, String s, StringBuilder sb) {
        if (index == s.length()) {
            result.add(sb.toString());
            return;
        }
        char ch = s.charAt(index);
        sb.append(ch);
        backtrack(result, index + 1, s, sb);
        sb.deleteCharAt(sb.length() - 1);
        if (!Character.isDigit(ch)) {
            ch = (char) (ch - 'a' >= 0 ? ch - 32 : ch + 32);
            sb.append(ch);
            backtrack(result, index + 1, s, sb);
            sb.deleteCharAt(sb.length() - 1);
        }
    }

}
