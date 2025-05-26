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
        int step = 13;
        System.out.println(climbStairs(step));
    }

    /**
     * 找规律，从第 3 阶开始，解决方案有 n-1 + n-2 种。
     * 1 阶楼梯时，解决方案为 1
     * 2 阶楼梯时，解决方案为 2
     * 3 阶楼梯时，n-1 + n-2 = 1 + 2 = 3
     * 4 阶楼梯时，n-1 + n-2 = 2 + 3 = 5
     * 5 阶楼梯时，n-1 + n-2 = 3 + 5 = 8
     * 6 阶楼梯时，n-1 + n-2 = 5 + 8 = 13
     * 7 阶楼梯时，n-1 + n-2 = 8 + 13 = 21
     * ...
     * <p>
     * 执行用时分布0ms击败100.00%
     * 内存消耗：38.2 MB, 在所有 Java 提交中击败了5.37%的用户
     * 消耗内存分布39.63MB击败35.69%
     */
    public int climbStairs(int n) {
        if (n <= 3) return n;
        int a = 1, b = 2, sum = a + b;
        while (n-- > 3) {
            a = b;
            b = sum;
            sum = a + b;
        }
        return sum;
    }

}
