package com.android.algorithm.leetcode;


import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;

public class Medium_567_字符串的排列 {

    @Test
    public void main() {
        String s1 = "hello";
        String s2 = "heollooo";
        System.out.println(checkInclusion2(s1, s2));
    }

    /**
     * 执行结果：通过
     * 执行用时：1723 ms, 在所有 Java 提交中击败了4.41%的用户
     * 内存消耗：39 MB, 在所有 Java 提交中击败了13.40%的用户
     */
    public boolean checkInclusion(String s1, String s2) {
        int len = s1.length();
        char[] chars = s2.toCharArray();
        char[] chars1 = s1.toCharArray();
        HashMap<Character, Integer> hashMap = new HashMap<>();
        for (int i = 0; i <= chars.length - len; i++) {
            hashMap.clear();
            char[] copyOfRange = Arrays.copyOfRange(chars, i, i + len);
            for (char c : chars1)
                if (hashMap.containsKey(c)) hashMap.put(c, hashMap.get(c) + 1);
                else hashMap.put(c, 1);
            for (char c : copyOfRange)
                if (hashMap.containsKey(c)) {
                    if (hashMap.get(c) == 1) hashMap.remove(c);
                    else hashMap.put(c, hashMap.get(c) - 1);
                }
            if (hashMap.size() == 0) return true;
        }
        return false;
    }

    /**
     * 执行结果：通过
     * 执行用时：800 ms, 在所有 Java 提交中击败了7.11%的用户
     * 内存消耗：38.8 MB, 在所有 Java 提交中击败了13.40%的用户
     */
    public boolean checkInclusion2(String s1, String s2) {
        int len = s1.length();
        char[] chars1 = s1.toCharArray();
        char[] chars2 = s2.toCharArray();
        Arrays.sort(chars1);
        for (int i = 0; i <= chars2.length - len; i++) {
            char[] copyOfRange = Arrays.copyOfRange(chars2, i, i + len);
            Arrays.sort(copyOfRange);
            if (Arrays.equals(chars1, copyOfRange)) return true;
        }
        return false;
    }

}
