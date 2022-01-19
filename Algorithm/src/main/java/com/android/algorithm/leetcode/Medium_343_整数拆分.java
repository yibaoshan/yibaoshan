package com.android.algorithm.leetcode;

import org.junit.Test;

public class Medium_343_整数拆分 {

    /**
     * 给定一个正整数 n，将其拆分为至少两个正整数的和，并使这些整数的乘积最大化。 返回你可以获得的最大乘积。
     * <p>
     * 示例 1:
     * <p>
     * 输入: 2
     * 输出: 1
     * 解释: 2 = 1 + 1, 1 × 1 = 1。
     * <p>
     * 来源：力扣（LeetCode）
     * 链接：https://leetcode-cn.com/problems/integer-break
     * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
     */

    @Test
    public void main() {
        System.out.println(integerBreak(10));
    }

    /**
     * 贪心
     * 执行结果：通过
     * 执行用时：0 ms, 在所有 Java 提交中击败了100.00%的用户
     * 内存消耗：35.1 MB, 在所有 Java 提交中击败了55.95%的用户
     */
    public int integerBreak(int n) {
        if (n < 4) return n - 1;
        int a = 1;
        while (n > 4) {
            n -= 3;
            a *= 3;
        }
        return a * n;
    }

}
