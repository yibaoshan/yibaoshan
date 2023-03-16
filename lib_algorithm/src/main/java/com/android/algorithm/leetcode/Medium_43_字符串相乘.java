package com.android.algorithm.leetcode;

import org.junit.Test;

public class Medium_43_字符串相乘 {

    /**
     * 给定两个以字符串形式表示的非负整数 num1 和 num2，返回 num1 和 num2 的乘积，它们的乘积也表示为字符串形式。
     * <p>
     * 示例 1:
     * <p>
     * 输入: num1 = "2", num2 = "3"
     * 输出: "6"
     * <p>
     * 来源：力扣（LeetCode）
     * 链接：https://leetcode-cn.com/problems/multiply-strings
     * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
     */

    @Test
    public void main() {
        String num1 = "2";
        String num2 = "12";
        System.out.println(multiply(num1, num2));
    }

    /**
     * 执行结果：通过
     * 执行用时：1 ms, 在所有 Java 提交中击败了100.00%的用户
     * 内存消耗：38.2 MB, 在所有 Java 提交中击败了93.58%的用户
     */
    public String multiply(String num1, String num2) {
        if (num1 == null || num2 == null) return null;
        int len1 = num1.length(), len2 = num2.length();
        if (len1 == 1 && num1.charAt(0) - '0' == 0
                || len2 == 1 && num2.charAt(0) - '0' == 0) {
            return "0";
        }
        int[] sums = new int[len1 + len2];

        int[] nums1 = new int[len1];
        for (int i = 0; i < len1; i++) {
            nums1[i] = num1.charAt(i) - '0';
        }
        for (int i = 0; i < len2; i++) {
            int multiply1 = num2.charAt(len2 - i - 1) - '0';
            for (int j = 0; j < len1; j++) {
                sums[i + j] += (multiply1 * nums1[len1 - j - 1]);
            }
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < sums.length; i++) {
            if (sums[i] >= 10) {
                sums[i + 1] += sums[i] / 10;
                sums[i] = sums[i] % 10;
            }
            if (i == sums.length - 1 && sums[i] == 0) {
                continue;
            }
            sb.append(sums[i]);
        }
        return sb.reverse().toString();
    }

}
