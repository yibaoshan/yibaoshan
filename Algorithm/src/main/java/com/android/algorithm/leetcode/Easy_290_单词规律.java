package com.android.algorithm.leetcode;

import org.junit.Test;

import java.util.HashMap;

public class Easy_290_单词规律 {

    /**
     * 给定一种规律 pattern 和一个字符串 str ，判断 str 是否遵循相同的规律。
     * <p>
     * 这里的 遵循 指完全匹配，例如， pattern 里的每个字母和字符串 str 中的每个非空单词之间存在着双向连接的对应规律。
     * <p>
     * 来源：力扣（LeetCode）
     * 链接：https://leetcode-cn.com/problems/word-pattern
     * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
     */

    @Test
    public void main() {
        String pattern = "abbc";
        String str = "dog cat cat dog";
        System.out.println(wordPattern(pattern, str));
    }

    /**
     * 执行结果：通过
     * 执行用时：1 ms, 在所有 Java 提交中击败了90.37%的用户
     * 内存消耗：36.3 MB, 在所有 Java 提交中击败了48.01%的用户
     */
    public boolean wordPattern(String pattern, String s) {
        if (pattern == null || s == null) return false;
        char[] chars = pattern.toCharArray();
        String[] splits = s.split(" ");
        if (chars.length != splits.length) return false;
        HashMap<Character, String> hashMap1 = new HashMap<>();
        HashMap<String, Character> hashMap2 = new HashMap<>();
        for (int i = 0; i < chars.length; i++) {
            if (hashMap1.containsKey(chars[i]) && !hashMap1.get(chars[i]).equals(splits[i])) return false;
            if (hashMap2.containsKey(splits[i]) && !hashMap2.get(splits[i]).equals(chars[i])) return false;
            hashMap1.put(chars[i], splits[i]);
            hashMap2.put(splits[i], chars[i]);
        }
        return true;
    }

}
