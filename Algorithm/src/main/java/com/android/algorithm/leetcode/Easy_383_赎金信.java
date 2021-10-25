package com.android.algorithm.leetcode;

import org.junit.Test;

import java.util.HashMap;
import java.util.HashSet;

public class Easy_383_赎金信 {

    /**
     * 给定一个赎金信 (ransom) 字符串和一个杂志(magazine)字符串，判断第一个字符串 ransom 能不能由第二个字符串 magazines 里面的字符构成。如果可以构成，返回 true ；否则返回 false。
     * <p>
     * (题目说明：为了不暴露赎金信字迹，要从杂志上搜索各个需要的字母，组成单词来表达意思。杂志字符串中的每个字符只能在赎金信字符串中使用一次。)
     * <p>
     *  
     * <p>
     * 示例 1：
     * <p>
     * 输入：ransomNote = "a", magazine = "b"
     * 输出：false
     * 示例 2：
     * <p>
     * 输入：ransomNote = "aa", magazine = "ab"
     * 输出：false
     * 示例 3：
     * <p>
     * 输入：ransomNote = "aa", magazine = "aab"
     * 输出：true
     * <p>
     * 来源：力扣（LeetCode）
     * 链接：https://leetcode-cn.com/problems/ransom-note
     * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
     */

    @Test
    public void main() {
        String ransomNote = "aa";
        String magazine = "ab";
        System.out.println(canConstruct(ransomNote, magazine));
    }

    /**
     * 执行结果：通过
     * 执行用时：11 ms, 在所有 Java 提交中击败了21.81%的用户
     * 内存消耗：39 MB, 在所有 Java 提交中击败了23.42%的用户
     */
    public boolean canConstruct(String ransomNote, String magazine) {
        HashMap<Character, Integer> hashMap = new HashMap<>();
        char[] chars = magazine.toCharArray();
        for (char c : chars) {
            int count = 0;
            if (hashMap.containsKey(c)) count = hashMap.get(c);
            hashMap.put(c, ++count);
        }
        char[] chars1 = ransomNote.toCharArray();
        for (char c : chars1) {
            if (hashMap.containsKey(c)) {
                int count = hashMap.get(c);
                if (count > 0) {
                    hashMap.put(c, --count);
                    continue;
                }
            }
            return false;
        }
        return true;
    }

}
