package com.android.algorithm.leetcode;

import org.junit.Test;

public class Medium_91_解码方法 {

    /**
     * 一条包含字母 A-Z 的消息通过以下映射进行了 编码 ：
     * <p>
     * 'A' -> 1
     * 'B' -> 2
     * ...
     * 'Z' -> 26
     * 要 解码 已编码的消息，所有数字必须基于上述映射的方法，反向映射回字母（可能有多种方法）。例如，"11106" 可以映射为：
     * <p>
     * "AAJF" ，将消息分组为 (1 1 10 6)
     * "KJF" ，将消息分组为 (11 10 6)
     * 注意，消息不能分组为  (1 11 06) ，因为 "06" 不能映射为 "F" ，这是由于 "6" 和 "06" 在映射中并不等价。
     * <p>
     * 给你一个只含数字的 非空 字符串 s ，请计算并返回 解码 方法的 总数 。
     * <p>
     * 题目数据保证答案肯定是一个 32 位 的整数。
     * <p>
     * 来源：力扣（LeetCode）
     * 链接：https://leetcode-cn.com/problems/decode-ways
     * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
     */

    @Test
    public void main() {
        String s = "10";
        System.out.println(numDecodings(s));
    }

    /**
     上楼梯的复杂版？
     如果连续的两位数符合条件，就相当于一个上楼梯的题目，可以有两种选法：
     1.一位数决定一个字母
     2.两位数决定一个字母
     就相当于dp(i) = dp[i-1] + dp[i-2];
     如果不符合条件，又有两种情况
     1.当前数字是0：
     不好意思，这阶楼梯不能单独走，
     dp[i] = dp[i-2]
     2.当前数字不是0
     不好意思，这阶楼梯太宽，走两步容易扯着步子，只能一个一个走
     dp[i] = dp[i-1];

     */
    /**
     * 执行结果：通过
     * 执行用时：0 ms, 在所有 Java 提交中击败了100.00%的用户
     * 内存消耗：36.5 MB, 在所有 Java 提交中击败了65.91%的用户
     */
    public int numDecodings(String s) {
        if (s == null || s.isEmpty()) return 0;
        final int length = s.length();
        if (length == 0) return 0;
        if (s.charAt(0) == '0') return 0;
        int[] dp = new int[length + 1];
        dp[0] = 1;
        for (int i = 0; i < length; i++) {
            dp[i + 1] = s.charAt(i) == '0' ? 0 : dp[i];
            if (i > 0 && (s.charAt(i - 1) == '1' || (s.charAt(i - 1) == '2' && s.charAt(i) <= '6'))) {
                dp[i + 1] += dp[i - 1];
            }
        }
        return dp[length];
    }

}
