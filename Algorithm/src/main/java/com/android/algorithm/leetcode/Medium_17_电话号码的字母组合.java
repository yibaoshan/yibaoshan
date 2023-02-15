package com.android.algorithm.leetcode;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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
        String digits = "23";
        List<String> list = letterCombinations(digits);
        for (String str : list) System.out.println(str);
    }

    private final List<String> list = new ArrayList<>();
    private final StringBuilder stringBuilder = new StringBuilder();

    public List<String> letterCombinations(String digits) {
        if (digits == null || digits.isEmpty()) return new ArrayList<>();
        List<List<String>> letters = new ArrayList<>();
        for (int i = 0; i < digits.length(); i++) {
            letters.add(num2char(digits.charAt(i)));
        }
        boolean[] visited = new boolean[letters.size()];
        backtrack(letters, 0, digits.length(), visited);
        return list;
    }

    private void backtrack(List<List<String>> letters, int start, int cnt, boolean[] visited) {
        if (stringBuilder.length() == cnt) {
            list.add(stringBuilder.toString());
            return;
        }
        for (int i = start; i < letters.size(); i++) {
            if (visited[i]) continue;
            visited[i] = true;
            for (int j = 0; j < letters.get(i).size(); j++) {
                stringBuilder.append(letters.get(i).get(j));
                backtrack(letters, start + 1, cnt, visited);
                stringBuilder.delete(stringBuilder.length() - 1, stringBuilder.length());
            }
            visited[i] = false;
        }
    }

    private List<String> num2char(char c) {
        List<String> ret = new ArrayList<>();
        switch (c) {
            case '2':
                Collections.addAll(ret, "a", "b", "c");
                break;
            case '3':
                Collections.addAll(ret, "d", "e", "f");
                break;
            case '4':
                Collections.addAll(ret, "g", "h", "i");
                break;
            case '5':
                Collections.addAll(ret, "j", "k", "l");
                break;
            case '6':
                Collections.addAll(ret, "m", "n", "o");
                break;
            case '7':
                Collections.addAll(ret, "p", "q", "r", "s");
                break;
            case '8':
                Collections.addAll(ret, "t", "u", "v");
                break;
            case '9':
                Collections.addAll(ret, "w", "x", "y", "z");
                break;
        }
        return ret;
    }

}
