package com.android.algorithm.leetcode;

import org.junit.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Medium_187_重复的DNA序列 {

    /**
     * 所有 DNA 都由一系列缩写为 'A'，'C'，'G' 和 'T' 的核苷酸组成，例如："ACGAATTCCG"。在研究 DNA 时，识别 DNA 中的重复序列有时会对研究非常有帮助。
     * <p>
     * 编写一个函数来找出所有目标子串，目标子串的长度为 10，且在 DNA 字符串 s 中出现次数超过一次。
     * <p>
     * 来源：力扣（LeetCode）
     * 链接：https://leetcode-cn.com/problems/repeated-dna-sequences
     * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
     */

    @Test
    public void main() {
        String s = "AAAAACCCCCAAAAACCCCCCAAAAAGGGTTT";
        List<String> res = findRepeatedDnaSequences(s);
        for (String str : res) System.out.println(str);
    }

    /**
     * 执行结果：通过
     * 执行用时：2 ms, 在所有 Java 提交中击败了99.77%的用户
     * 内存消耗：38.6 MB, 在所有 Java 提交中击败了98.77%的用户
     */
    public List<String> findRepeatedDnaSequences(String s) {
        if (s.length() <= 10 || s.length() > 10000) return new ArrayList<>();
        Set<String> ans = new HashSet<>(), set = new HashSet<>();
        char[] chars = s.toCharArray();
        int left, right;
        for (int i = 0; i < 10; i++) {
            left = right = i;
            while (right <= s.length() - 10) {
                right += 10;
                String str = String.valueOf(chars, left, 10);
                if (!set.add(str)) ans.add(str);
                left = right;
            }
        }
        return new ArrayList<>(ans);
    }

}
