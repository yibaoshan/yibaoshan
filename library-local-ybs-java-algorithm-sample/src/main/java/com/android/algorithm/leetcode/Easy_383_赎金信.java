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
        String magazine = "aab";
        System.out.println(canConstruct(ransomNote, magazine));
    }

    /**
     * 执行结果：通过
     * 执行用时：11 ms, 在所有 Java 提交中击败了21.81%的用户
     * 内存消耗：39 MB, 在所有 Java 提交中击败了23.42%的用户
     */
    public boolean canConstruct(String ransomNote, String magazine) {
        /**
         * 简单题，利用 hashmap 对 magazine 字符串的每个字符出现次数
         *
         * 接着，遍历 ransomNote 字符串，判断 hashmap 保存的每个字符数量是否够用，不够直接返回 false
         *
         * 最差时间复杂度 O(m+n)
         *
         * */
        HashMap<Character, Integer> hashMap = new HashMap<>();
        // 利用 hashMap 保存每个字符出现的次数
        for (int i = 0; i < magazine.length(); i++) {
            if (hashMap.containsKey(magazine.charAt(i))) hashMap.put(magazine.charAt(i), hashMap.get(magazine.charAt(i)) + 1);
            else hashMap.put(magazine.charAt(i), 1);
        }
        // 比对过程，如果字符不够用，及时返回 false
        for (int i = 0; i < ransomNote.length(); i++) {
            if (hashMap.containsKey(ransomNote.charAt(i))) {
                hashMap.put(ransomNote.charAt(i), hashMap.get(ransomNote.charAt(i)) - 1);
                if (hashMap.get(ransomNote.charAt(i)) < 0) return false;
            } else return false;
        }
        return true;
    }

}
