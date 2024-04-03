package com.android.algorithm.leetcode;

import org.junit.Test;

public class Easy_L7_整数翻转 {

    /**
     * 给你一个 32 位的有符号整数 x ，返回将 x 中的数字部分反转后的结果。
     *
     * 如果反转后整数超过 32 位的有符号整数的范围 [−231,  231 − 1] ，就返回 0。
     *
     * 假设环境不允许存储 64 位整数（有符号或无符号）。
     *
     * 来源：力扣（LeetCode）
     * 链接：https://leetcode-cn.com/problems/reverse-integer
     * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
     */

    @Test
    public void main() {
        System.out.println(reverse(1534236469));
    }

    /**
     * 执行结果：通过
     * 执行用时：1 ms, 在所有 Java 提交中击败了100.00%的用户
     * 内存消耗：5.5 MB, 在所有 Java 提交中击败了65.01%的用户
     * */
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
