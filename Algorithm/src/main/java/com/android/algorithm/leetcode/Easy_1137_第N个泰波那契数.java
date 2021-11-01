package com.android.algorithm.leetcode;

import org.junit.Test;

public class Easy_1137_第N个泰波那契数 {

    /**
     * 泰波那契序列 Tn 定义如下： 
     * <p>
     * T0 = 0, T1 = 1, T2 = 1, 且在 n >= 0 的条件下 Tn+3 = Tn + Tn+1 + Tn+2
     * <p>
     * 给你整数 n，请返回第 n 个泰波那契数 Tn 的值。
     * <p>
     *  
     * <p>
     * 示例 1：
     * <p>
     * 输入：n = 4
     * 输出：4
     * 解释：
     * T_3 = 0 + 1 + 1 = 2
     * T_4 = 1 + 1 + 2 = 4
     * 示例 2：
     * <p>
     * 输入：n = 25
     * 输出：1389537
     * <p>
     * 来源：力扣（LeetCode）
     * 链接：https://leetcode-cn.com/problems/n-th-tribonacci-number
     * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
     */
    @Test
    public void main() {
        System.out.println(tribonacci(25));
    }

    /**
     * 官解答案
     * 执行结果：通过
     * 执行用时：0 ms, 在所有 Java 提交中击败了100.00%的用户
     * 内存消耗：35.1 MB, 在所有 Java 提交中击败了57.20%的用户
     */
    public int tribonacci(int n) {
        if (n == 0) return 0;
        if (n <= 2) return 1;
        int p, q = 0, r = 1, result = 1;
        for (int i = 3; i <= n; ++i) {
            p = q;
            q = r;
            r = result;
            result = p + q + r;
        }
        return result;
    }

}
