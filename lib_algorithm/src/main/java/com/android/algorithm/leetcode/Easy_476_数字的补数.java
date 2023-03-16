package com.android.algorithm.leetcode;

import org.junit.Test;

public class Easy_476_数字的补数 {

    /**
     * 给你一个 正 整数 num ，输出它的补数。补数是对该数的二进制表示取反。
     * <p>
     * 示例 1：
     * <p>
     * 输入：num = 5
     * 输出：2
     * 解释：5 的二进制表示为 101（没有前导零位），其补数为 010。所以你需要输出 2 。
     * 示例 2：
     * <p>
     * 输入：num = 1
     * 输出：0
     * 解释：1 的二进制表示为 1（没有前导零位），其补数为 0。所以你需要输出 0 。
     * <p>
     * <p>
     * 来源：力扣（LeetCode）
     * 链接：https://leetcode-cn.com/problems/number-complement
     * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
     */
    @Test
    public void main() {
        System.out.println(findComplement(5));
    }

    /**
     * 执行结果：通过
     * 执行用时：0 ms, 在所有 Java 提交中击败了100.00%的用户
     * 内存消耗：35.1 MB, 在所有 Java 提交中击败了83.70%的用户
     */
    public int findComplement(int num) {
        int t = num;
        t |= t >> 1;
        t |= t >> 2;
        t |= t >> 4;
        t |= t >> 8;
        t |= t >> 16;
        return ~num & t;
    }

}
