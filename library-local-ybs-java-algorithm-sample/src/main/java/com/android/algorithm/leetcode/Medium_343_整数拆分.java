package com.android.algorithm.leetcode;

import org.junit.Test;

import java.util.Arrays;

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
        System.out.println(integerBreak2(10));
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

    /**
     * 动态规划，随想录的代码，暂时无法理解
     */
    public int integerBreak2(int n) {
        if (n < 4) return n - 1;
        int[] dp = new int[n + 1];
        dp[2] = 1;
        for (int i = 3; i <= n; ++i) {
            for (int j = 1; j < i - 1; ++j) {
                System.out.println("i=" + i + ",j=" + j + ",array=" + Arrays.toString(dp));
                //j*(i-j)代表把i拆分为j和i-j两个数相乘
                //j*dp[i-j]代表把i拆分成j和继续把(i-j)这个数拆分，取(i-j)拆分结果中的最大乘积与j相乘
                int a = j * (i - j);
                int b = j * dp[i - j];
                dp[i] = Math.max(dp[i], Math.max(a, b));
            }
        }
        return dp[n];
    }

}
