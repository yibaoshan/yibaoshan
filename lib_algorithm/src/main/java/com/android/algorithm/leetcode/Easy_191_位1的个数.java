package com.android.algorithm.leetcode;

import org.junit.Test;

public class Easy_191_位1的个数 {

    /**
     * 编写一个函数，输入是一个无符号整数（以二进制串的形式），返回其二进制表达式中数字位数为 '1' 的个数（也被称为汉明重量）。
     * <p>
     *  
     * <p>
     * 提示：
     * <p>
     * 请注意，在某些语言（如 Java）中，没有无符号整数类型。在这种情况下，输入和输出都将被指定为有符号整数类型，并且不应影响您的实现，因为无论整数是有符号的还是无符号的，其内部的二进制表示形式都是相同的。
     * 在 Java 中，编译器使用二进制补码记法来表示有符号整数。因此，在上面的 示例 3 中，输入表示有符号整数 -3。
     * <p>
     * <p>
     * 来源：力扣（LeetCode）
     * 链接：https://leetcode-cn.com/problems/number-of-1-bits
     * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
     */

    @Test
    public void main() {
        System.out.println(hammingWeight(2));
        System.out.println(hammingWeight2(3));
    }

    /**
     * 执行结果：通过
     * 执行用时：0 ms, 在所有 Java 提交中击败了100.00%的用户
     * 内存消耗：34.9 MB, 在所有 Java 提交中击败了98.67%的用户
     */
    // you need to treat n as an unsigned value
    public int hammingWeight(int n) {
        return Integer.bitCount(n);
    }

    public int hammingWeight2(int var0) {
        var0 -= var0 >>> 1 & 1431655765;
        var0 = (var0 & 858993459) + (var0 >>> 2 & 858993459);
        var0 = var0 + (var0 >>> 4) & 252645135;
        var0 += var0 >>> 8;
        var0 += var0 >>> 16;
        return var0 & 63;
    }

    public int hammingWeight3(int n) {
        int ret = 0;
        for (int i = 0; i < 32; i++) {
            if ((n & (1 << i)) != 0) {
                ret++;
            }
        }
        return ret;
    }

}
