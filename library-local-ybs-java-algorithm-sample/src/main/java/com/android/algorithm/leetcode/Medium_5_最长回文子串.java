package com.android.algorithm.leetcode;

import org.junit.Test;

import java.util.Arrays;

public class Medium_5_最长回文子串 {

    /**
     * 给你一个字符串 s，找到 s 中最长的回文子串。
     * 示例 1：
     * <p>
     * 输入：s = "babad"
     * 输出："bab"
     * 解释："aba" 同样是符合题意的答案。
     * 示例 2：
     * <p>
     * 输入：s = "cbbd"
     * 输出："bb"
     * 示例 3：
     * <p>
     * 输入：s = "a"
     * 输出："a"
     * 示例 4：
     * <p>
     * 输入：s = "ac"
     * 输出："a"
     * <p>
     * 来源：力扣（LeetCode）
     * 链接：https://leetcode-cn.com/problems/longest-palindromic-substring
     * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
     */

    @Test
    public void main() {
        System.out.println(longestPalindrome("abcdc"));
        System.out.println(longestPalindrome2("abcdc"));
        System.out.println(isPalindrome(new char[]{1, 0, 1, 1}));
    }

    /*
最长回文子串，经典题目，直接暴力遍历求解，也可以用hash，思路如下：
以字符串s = "babad"举例，先写个方法判断是否是回文字符串
1. 从最长的字符串长度开始遍历，length = 5时字符串为"babad"，不是回文
2. length = 4时，字符串有两个，分别判断"baba"、"abad"，都不是回文
3. length = 3时，有三种字符串组合，第一个是"bab"，哎，找到了
由于遍历长度是递减的，所以一旦找到回文子串，那必然是最长的回文子串
*/
    public String longestPalindrome3(String s) {
        if(s==null||s.length()<2)return s;
        int length = s.length();
        while(length>=0){
            for(int i = 0;i <s.length();i++){
                if(i+length>=s.length())break;
                if(isPalindrome(s,i,i+length))return s.substring(i,i+length+1);
            }
            length--;
        }
        return null;
    }

    //判定是否是回文字符串，为了避免浪费内存空间，要求传入下标在字符串内部操作
    public boolean isPalindrome(String s,int start,int end){
        //System.out.println(s.substring(start,end+1));
        while(start<=end){
            if(s.charAt(start)!=s.charAt(end))return false;
            end--;
            start++;
        }
        return true;
    }


    /**
     * 滑动窗口思想：从左到右按步进长度依次遍历，步进长度从字符串长度开始然后递减
     * String s = "abcdc"
     * 步进长度为5
     * [a, b, c, d, c]false
     * 步进长度为4
     * [a, b, c, d]false
     * [b, c, d, c]false
     * 步进长度为3
     * [a, b, c]false
     * [b, c, d]false
     * [c, d, c]true 发现回文串，因为是长度是从大到小遍历过来，这里必然是最长回文子串
     * 步进长度为2
     * [a, b]false
     * [b, c]false
     * [c, d]false
     * [d, c]false
     * 步进长度为1
     * [a]true
     * [b]true
     * [c]true
     * [d]true
     * [c]true
     * <p>
     * 执行结果：通过
     * 执行用时：934 ms, 在所有 Java 提交中击败了5.01%的用户
     * 内存消耗：39.1 MB, 在所有 Java 提交中击败了49.25%的用户
     */
    public String longestPalindrome(String s) {
        if (s == null) return null;
        if (s.length() == 1) return s;
        char[] chars = s.toCharArray();
        if (s.length() == 2) {
            if (chars[0] == chars[1]) return s;
            else return String.valueOf(chars[0]);
        }
        int stepping = chars.length;
        while (stepping > 0) {
            int start = 0;
            while (stepping + start <= chars.length) {
                char[] once = Arrays.copyOfRange(chars, start, start + stepping);
                if (isPalindrome(once)) return String.valueOf(once);
                start++;
            }
            stepping--;
        }
        return null;
    }

    /**
     * dp[i][j]表示子串i～j是否是回文子串，循环s的子串，看是否满足s[i]，s[j]相等，
     * 如果相等，则dp[i][j]是否为回文串取决于dp[i+1][j-1]是否也是回文子串，
     * 在循环的过程中不断更新最大回文子串的长度，注意子串的长度是0或1也算回文子串
     */
    public String longestPalindrome2(String s) {
        if (s == null) return null;
        if (s.length() == 1) return s;
        char[] chars = s.toCharArray();
        if (s.length() == 2) {
            if (chars[0] == chars[1]) return s;
            else return String.valueOf(chars[0]);
        }
        int[][] dp = new int[s.length()][s.length()];
        for (int i = 0; i < dp.length; i++) {
            for (int j = 0; j < dp[i].length; j++) {
                if (s.charAt(i) == s.charAt(j)) {
                    dp[i][j] = 1;
                    if (i + 1 < dp.length && j - 1 >= 0) {
                        if (s.charAt(i + 1) == s.charAt(j - 1)) {
                            System.out.println(s.charAt(i + 1) + "," + s.charAt(j - 1));
                            dp[i][j] += dp[i + 1][j - 1];
                        }
                    }
                }
            }
        }
        System.out.println(Arrays.toString(s.toCharArray()));
        LeetCodeUtil.print(dp);
        return "";
    }

    private boolean isPalindrome(char[] chars) {
        int left = 0, right = chars.length - 1;
        while (right >= left) {
            if (chars[left] != chars[right]) return false;
            left++;
            right--;
        }
        return true;
    }

}
