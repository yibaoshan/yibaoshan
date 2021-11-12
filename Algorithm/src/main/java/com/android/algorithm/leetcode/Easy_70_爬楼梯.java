package com.android.algorithm.leetcode;

import org.junit.Test;

public class Easy_70_爬楼梯 {

    /**
     * 假设你正在爬楼梯。需要 n 阶你才能到达楼顶。
     * <p>
     * 每次你可以爬 1 或 2 个台阶。你有多少种不同的方法可以爬到楼顶呢？
     * <p>
     * 注意：给定 n 是一个正整数。
     * <p>
     * 示例 1：
     * <p>
     * 输入： 2
     * 输出： 2
     * 解释： 有两种方法可以爬到楼顶。
     * 1.  1 阶 + 1 阶
     * 2.  2 阶
     * <p>
     * 来源：力扣（LeetCode）
     * 链接：https://leetcode-cn.com/problems/climbing-stairs
     * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
     */

    @Test
    public void main() {
        int step = 4;
        System.out.println(climbStairs(step));
        System.out.println(climbStairs2(step));
    }

    /**
     * 执行结果：超时
     */
    public int climbStairs(int n) {
        if (n < 3) return n;
        return climbStairs(n - 1) + climbStairs(n - 2);
    }

    /**
     * 动态规划，评论区答案，这一次的结果依赖于上一次计算结果
     * 执行结果：通过
     * 执行用时：0 ms, 在所有 Java 提交中击败了100.00%的用户
     * 内存消耗：34.9 MB, 在所有 Java 提交中击败了90.06%的用户
     */
    public int climbStairs2(int n) {
        if (n < 3) return n;
        int one = 1, two = 2, temp;
        for (int i = 3; i <= n; i++) {
            temp = one;
            one = two;
            two = temp + one;
        }
        return two;
    }

}
