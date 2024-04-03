package com.android.algorithm.leetcode;

import org.junit.Test;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class Medium_438_找到字符串中所有字母异位词 {

    /**
     * 给定两个字符串 s 和 p，找到 s 中所有 p 的 异位词 的子串，返回这些子串的起始索引。不考虑答案输出的顺序。
     * <p>
     * 异位词 指由相同字母重排列形成的字符串（包括相同的字符串）。
     * <p>
     * 来源：力扣（LeetCode）
     * 链接：https://leetcode-cn.com/problems/find-all-anagrams-in-a-string
     * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
     */

    @Test
    public void main() {
        String s = "cbaebabacd";
        String p = "abc";
        List<Integer> anagrams = findAnagrams(s, p);
        for (int i = 0; i < anagrams.size(); i++) {
            System.out.println(anagrams.get(i));
        }
    }

    /**
     * 执行结果：通过
     * 执行用时：1548 ms, 在所有 Java 提交中击败了5.08%的用户
     * 内存消耗：40.5 MB, 在所有 Java 提交中击败了5.8%的用户
     */
    public List<Integer> findAnagrams(String s, String p) {
        List<Integer> res = new LinkedList<>();
        if (s == null || p == null) return res;
        char[] pChars = p.toCharArray();
        char[] sChars = s.toCharArray();
        Arrays.sort(pChars);
        for (int i = 0; i < sChars.length; i++) {
            if (i > sChars.length - pChars.length) break;
            char[] chars = Arrays.copyOfRange(sChars, i, i + pChars.length);
            Arrays.sort(chars);
            if (Arrays.equals(chars, pChars)) res.add(i);
        }
        return res;
    }

}
