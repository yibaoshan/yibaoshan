package com.android.algorithm.leetcode;

import org.junit.Test;

import java.util.Arrays;

public class Easy_剑指_Offer_58_2_左旋转字符串 {

    /**
     * 字符串的左旋转操作是把字符串前面的若干个字符转移到字符串的尾部。请定义一个函数实现字符串左旋转操作的功能。
     * <p>
     * 比如，输入字符串"abcdefg"和数字2，该函数将返回左旋转两位得到的结果"cdefgab"。
     * <p>
     * 示例 1：
     * <p>
     * 输入: s = "abcdefg", k = 2
     * 输出: "cdefgab"
     * 示例 2：
     * <p>
     * 输入: s = "lrloseumgh", k = 6
     * 输出: "umghlrlose"
     * <p>
     * 来源：力扣（LeetCode）
     * 链接：https://leetcode.cn/problems/zuo-xuan-zhuan-zi-fu-chuan-lcof/
     * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
     */

    @Test
    public void main() {
        String s = "lrloseumgh";
        int k = 6;
        System.out.println(reverseLeftWords(s, k));
    }

    // ummm 还以为有什么坑呢，结果一遍过了。ps，还没来得及优化
    public String reverseLeftWords(String s, int n) {
        char[] ret = new char[s.length()];
        int index = 0;
        for (int i = n; i < s.length(); i++) {
            ret[index++] = s.charAt(i);
        }
        for (int i = 0; i < n; i++) {
            ret[index++] = s.charAt(i);
        }
        return new String(ret);
    }

}
