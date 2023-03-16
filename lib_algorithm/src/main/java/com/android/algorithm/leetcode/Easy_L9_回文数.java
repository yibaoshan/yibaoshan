package com.android.algorithm.leetcode;

import org.junit.Test;

public class Easy_L9_回文数 {

    /**
     * 给你一个整数 x ，如果 x 是一个回文整数，返回 true ；否则，返回 false 。
     * <p>
     * 回文数是指正序（从左向右）和倒序（从右向左）读都是一样的整数。例如，121 是回文，而 123 不是。
     * <p>
     * 进阶：你能不将整数转为字符串来解决这个问题吗？
     * 来源：力扣（LeetCode）
     * 链接：https://leetcode-cn.com/problems/palindrome-number
     */

    @Test
    public void main() {
        int number = 1221;
        System.out.println(palindromeNumber2(number));
        System.out.println(reverse(1534236469));
    }

    /**
     * 执行结果：通过
     * 执行用时：10 ms, 在所有 Java 提交中击败了40.59%的用户
     * 内存消耗：38.3 MB, 在所有 Java 提交中击败了9.13%的用户
     * 通过测试用例：11510 / 11510
     */
    private boolean palindromeNumber(Integer number) {
        if (number < 0) return false;
        if (number < 10) return true;
        char[] chars = number.toString().toCharArray();
        if (number < 100) return chars[0] == chars[1];
        int leftIndex = chars.length / 2 - 1, rightIndex = leftIndex + 1;
        if (chars.length % 2 != 0) rightIndex++;
        while (leftIndex >= 0 && rightIndex < chars.length) {
            if (chars[leftIndex] != chars[rightIndex]) return false;
            leftIndex--;
            rightIndex++;
        }
        return true;
    }

    /**
     * 进阶版：不使用字符串，判断反转后的整数是否和反转前相等，不必考虑溢出
     * 执行结果：通过
     * 执行用时：9 ms, 在所有 Java 提交中击败了67.02%的用户
     * 内存消耗：38 MB, 在所有 Java 提交中击败了24.36%的用户
     */
    private boolean palindromeNumber2(Integer number) {
        if (number < 0) return false;
        if (number < 10) return true;
        return number == reverse(number);
    }

    /**
     * 整数反转
     *
     * @see com.android.algorithm.leetcode.Easy_L7_整数翻转
     */
    public int reverse(int x) {
        if (x == 0) return x;
        while (x % 10 == 0) x /= 10;
        int result = 0;
        while (x != 0) {
            if (result < Integer.MIN_VALUE / 10 || result > Integer.MAX_VALUE / 10) return 0;
            result = result * 10 + x % 10;
            x /= 10;
        }
        return result;
    }

}
