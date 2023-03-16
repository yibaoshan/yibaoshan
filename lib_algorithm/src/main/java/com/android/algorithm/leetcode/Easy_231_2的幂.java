package com.android.algorithm.leetcode;

import org.junit.Test;

public class Easy_231_2的幂 {

    /**
     * 给你一个整数 n，请你判断该整数是否是 2 的幂次方。如果是，返回 true ；否则，返回 false 。
     * <p>
     * 如果存在一个整数 x 使得 n == 2x ，则认为 n 是 2 的幂次方。
     * <p>
     * 来源：力扣（LeetCode）
     * 链接：https://leetcode-cn.com/problems/power-of-two
     * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
     */

    @Test
    public void main() {
        System.out.println(Integer.toBinaryString(1));
        System.out.println(Integer.toBinaryString(2));
        System.out.println(Integer.toBinaryString(4));
        System.out.println(Integer.toBinaryString(8));
        System.out.println(Integer.toBinaryString(16));
        System.out.println(Integer.toBinaryString(32));
        System.out.println(Integer.toBinaryString(32 & 32 - 1));

        System.out.println(isPowerOfTwo(1));
        System.out.println(isPowerOfTwo(2));
        System.out.println(isPowerOfTwo(4));
        System.out.println(isPowerOfTwo(6));
        System.out.println(isPowerOfTwo(8));
        System.out.println(isPowerOfTwo(10));
    }

    /**
     * 执行结果：通过
     * 执行用时：1 ms, 在所有 Java 提交中击败了94.45%的用户
     * 内存消耗：35.2 MB, 在所有 Java 提交中击败了93.07%的用户
     */
    public boolean isPowerOfTwo(int n) {
        if (n < 1) return false;
        return (n & n - 1) == 0;
    }

}
