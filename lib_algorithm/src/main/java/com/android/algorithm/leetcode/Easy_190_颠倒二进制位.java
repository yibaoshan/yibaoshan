package com.android.algorithm.leetcode;

import org.junit.Test;

public class Easy_190_颠倒二进制位 {

    /**
     * 颠倒给定的 32 位无符号整数的二进制位。
     * <p>
     * 提示：
     * <p>
     * 请注意，在某些语言（如 Java）中，没有无符号整数类型。在这种情况下，输入和输出都将被指定为有符号整数类型，并且不应影响您的实现，因为无论整数是有符号的还是无符号的，其内部的二进制表示形式都是相同的。
     * 在 Java 中，编译器使用二进制补码记法来表示有符号整数。因此，在 示例 2 中，输入表示有符号整数 -3，输出表示有符号整数 -1073741825。
     * <p>
     * <p>
     * 来源：力扣（LeetCode）
     * 链接：https://leetcode-cn.com/problems/reverse-bits
     * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
     */

    @Test
    public void main() {
        int n = 5;
        System.out.println(Integer.toBinaryString(n));
        System.out.println(Integer.toBinaryString(reverseBits(n)));
    }

    /**
     * 评论区答案
     * 执行结果：通过
     * 执行用时：1 ms, 在所有 Java 提交中击败了98.24%的用户
     * 内存消耗：38.1 MB, 在所有 Java 提交中击败了65.64%的用户
     */
    // you need treat n as an unsigned value
    public int reverseBits(int n) {
        int r = 0;
        for (int i = 0; i <= 31; i++) {
            r = r + ((1 & (n >> i)) << (31 - i));
        }
        return r;
    }

}
