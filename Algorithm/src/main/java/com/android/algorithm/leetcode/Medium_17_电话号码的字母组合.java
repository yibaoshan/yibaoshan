package com.android.algorithm.leetcode;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class Medium_17_电话号码的字母组合 {

    /**
     * 给定一个仅包含数字 2-9 的字符串，返回所有它能表示的字母组合。答案可以按 任意顺序 返回。
     * <p>
     * 给出数字到字母的映射如下（与电话按键相同）。注意 1 不对应任何字母。
     * <p>
     * <p>
     * 示例 1：
     * <p>
     * 输入：digits = "23"
     * 输出：["ad","ae","af","bd","be","bf","cd","ce","cf"]
     * <p>
     * 来源：力扣（LeetCode）
     * 链接：https://leetcode-cn.com/problems/letter-combinations-of-a-phone-number
     * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
     */

    @Test
    public void main() {
        String digits = "";
        List<String> list = letterCombinations(digits);
        for (String str : list) System.out.println(str);
    }

    private final StringBuilder sb = new StringBuilder();
    private final List<String> res = new ArrayList<>();

    /**
     * 回溯
     * 执行结果：通过
     * 执行用时：1 ms, 在所有 Java 提交中击败了61.02%的用户
     * 内存消耗：37.2 MB, 在所有 Java 提交中击败了44.47%的用户
     */
    public List<String> letterCombinations(String digits) {
        if (digits == null) return null;
        if (digits.isEmpty()) return res;
        char[] chars = digits.toCharArray();
        List<char[]> allChars = new ArrayList<>();
        for (int i = 0; i < chars.length; i++) {
            char[] cur = query(chars[i]);
            if (cur != null) allChars.add(cur);
        }
        boolean[] visited = new boolean[allChars.size()];
        dfs(allChars, allChars.size(), 0, visited);
        return res;
    }

    private void dfs(List<char[]> allChars, int n, int startIndex, boolean[] visited) {
        if (sb.length() == n) {
            res.add(sb.toString());
            return;
        }
        for (int i = startIndex; i < allChars.size(); i++) {
            if (visited[i]) continue;
            visited[i] = true;
            for (int j = 0; j < allChars.get(i).length; j++) {
                sb.append(allChars.get(i)[j]);
                dfs(allChars, n, i + 1, visited);
                sb.delete(sb.length() - 1, sb.length());
            }
            visited[i] = false;
        }
    }

    private char[] query(char c) {
        switch (c) {
            case '2':
                return new char[]{'a', 'b', 'c'};
            case '3':
                return new char[]{'d', 'e', 'f'};
            case '4':
                return new char[]{'g', 'h', 'i'};
            case '5':
                return new char[]{'j', 'k', 'l'};
            case '6':
                return new char[]{'m', 'n', 'o'};
            case '7':
                return new char[]{'p', 'q', 'r', 's'};
            case '8':
                return new char[]{'t', 'u', 'v'};
            case '9':
                return new char[]{'w', 'x', 'y', 'z'};
        }
        return null;
    }

}
