package com.android.algorithm.leetcode;

import org.junit.Test;

public class Easy_509_斐波那契数 {

    /**
     * 斐波那契数，通常用 F(n) 表示，形成的序列称为 斐波那契数列 。该数列由 0 和 1 开始，后面的每一项数字都是前面两项数字的和。也就是：
     * <p>
     * F(0) = 0，F(1) = 1
     * F(n) = F(n - 1) + F(n - 2)，其中 n > 1
     * 给你 n ，请计算 F(n) 。
     * <p>
     *  
     * <p>
     * 示例 1：
     * <p>
     * 输入：2
     * 输出：1
     * 解释：F(2) = F(1) + F(0) = 1 + 0 = 1
     * 示例 2：
     * <p>
     * 输入：3
     * 输出：2
     * 解释：F(3) = F(2) + F(1) = 1 + 1 = 2
     * 示例 3：
     * <p>
     * 输入：4
     * 输出：3
     * 解释：F(4) = F(3) + F(2) = 2 + 1 = 3
     * <p>
     * 来源：力扣（LeetCode）
     * 链接：https://leetcode-cn.com/problems/fibonacci-number
     * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
     */

    @Test
    public void main() {
        int n = 17;
        System.out.println(fib(n));
        System.out.println(fib2(n));
    }

    /**
     * 递归解法
     * 执行结果：通过
     * 执行用时：7 ms, 在所有 Java 提交中击败了25.74%的用户
     * 内存消耗：34.7 MB, 在所有 Java 提交中击败了98.75%的用户
     */
    public int fib(int n) {
        if (n <= 1) return n;
        return fib(n - 1) + fib(n - 2);
    }

    /**
     * 动态规划解法
     * 执行结果：通过
     * 执行用时：0 ms, 在所有 Java 提交中击败了100.00%的用户
     * 内存消耗：35.1 MB, 在所有 Java 提交中击败了61.64%的用户
     */
    public int fib2(int n) {
        if (n <= 1) return n;
        int count = 0, a = 0, b = 1;
        while (n-- > 1) {
            count = a + b;
            a = b;
            b = count;
        }
        return count;
    }

}
