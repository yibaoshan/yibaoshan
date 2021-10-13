package com.android.algorithm.leetcode;

import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

public class Medium_L3_无重复字符的最长子串 {

    /**
     * 给定一个字符串 s ，请你找出其中不含有重复字符的 最长子串 的长度。
     * 示例 1:
     * <p>
     * 输入: s = "abcabcbb"
     * 输出: 3
     * 解释: 因为无重复字符的最长子串是 "abc"，所以其长度为 3。
     * 示例 2:
     * <p>
     * 输入: s = "bbbbb"
     * 输出: 1
     * 解释: 因为无重复字符的最长子串是 "b"，所以其长度为 1。
     * 示例 3:
     * <p>
     * 输入: s = "pwwkew"
     * 输出: 3
     * 解释: 因为无重复字符的最长子串是 "wke"，所以其长度为 3。
     *      请注意，你的答案必须是 子串 的长度，"pwke" 是一个子序列，不是子串。
     * <p>
     * 来源：力扣（LeetCode）
     * 链接：https://leetcode-cn.com/problems/longest-substring-without-repeating-characters
     * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
     */

    @Test
    public void main() {
        String s = "abcabcbb";
        System.out.println(lengthOfLongestSubstring2(s));
    }

    /**
     * 执行结果：通过
     * 执行用时：14 ms, 在所有 Java 提交中击败了17.53%的用户
     * 内存消耗：39.1 MB, 在所有 Java 提交中击败了10.48%的用户
     */
    public int lengthOfLongestSubstring(String s) {
        if (s == null) return 0;
        char[] chars = s.toCharArray();
        String maxString = "";
        int maxInt = 0;
        for (char c : chars) {
            String string = String.valueOf(c);
            int stringIndexOf = maxString.indexOf(string);
            if (stringIndexOf > -1) {
                if (maxString.length() > maxInt) maxInt = maxString.length();
                maxString = maxString.substring(stringIndexOf + 1);
            }
            maxString += c;
        }
        if (maxString.length() > maxInt) maxInt = maxString.length();
        return maxInt;
    }

    /**
     * 使用滑动窗口思想，自己实现
     * 执行结果：通过
     * 执行用时：66 ms, 在所有 Java 提交中击败了12.86%的用户
     * 内存消耗：39.1 MB, 在所有 Java 提交中击败了9.78%的用户
     */
    public int lengthOfLongestSubstring2(String s) {
        char[] chars = s.toCharArray();
        HashSet<Character> hashSet = new HashSet<>();
        int left = 0, maxLength = 0;
        while (left < chars.length) {
            for (int i = left; i < chars.length; i++) {
                if (hashSet.contains(chars[i])) {
                    if (hashSet.size() > maxLength) maxLength = hashSet.size();
                    hashSet.clear();
                    break;
                }
                hashSet.add(chars[i]);
            }
            left++;
        }
        if (hashSet.size() > maxLength) maxLength = hashSet.size();
        return maxLength;
    }

    /**
     * 滑动窗口思想，官方解答
     * 执行结果：通过
     * 执行用时：6 ms, 在所有 Java 提交中击败了72.76%的用户
     * 内存消耗：38.6 MB, 在所有 Java 提交中击败了30.32%的用户
     */
    public int lengthOfLongestSubstring3(String s) {
        // 哈希集合，记录每个字符是否出现过
        Set<Character> occ = new HashSet<Character>();
        int n = s.length();
        // 右指针，初始值为 -1，相当于我们在字符串的左边界的左侧，还没有开始移动
        int rk = -1, ans = 0;
        for (int i = 0; i < n; ++i) {
            if (i != 0) {
                // 左指针向右移动一格，移除一个字符
                occ.remove(s.charAt(i - 1));
            }
            while (rk + 1 < n && !occ.contains(s.charAt(rk + 1))) {
                // 不断地移动右指针
                occ.add(s.charAt(rk + 1));
                ++rk;
            }
            // 第 i 到 rk 个字符是一个极长的无重复字符子串
            ans = Math.max(ans, rk - i + 1);
        }
        return ans;
    }

}
