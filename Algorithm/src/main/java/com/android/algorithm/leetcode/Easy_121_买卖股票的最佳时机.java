package com.android.algorithm.leetcode;

import org.junit.Test;

public class Easy_121_买卖股票的最佳时机 {

    /**
     * 给定一个数组 prices ，它的第 i 个元素 prices[i] 表示一支给定股票第 i 天的价格。
     * <p>
     * 你只能选择 某一天 买入这只股票，并选择在 未来的某一个不同的日子 卖出该股票。设计一个算法来计算你所能获取的最大利润。
     * <p>
     * 返回你可以从这笔交易中获取的最大利润。如果你不能获取任何利润，返回 0 。
     * <p>
     * 来源：力扣（LeetCode）
     * 链接：https://leetcode-cn.com/problems/best-time-to-buy-and-sell-stock
     * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
     */

    @Test
    public void main() {
        int[] prices = new int[]{2,4,1};
        System.out.println(maxProfit(prices));
    }

    //开始转变思路，最小值默认等于下标0
    //向后遍历时动态更改最小值的值，最小值就等于当前值和最小值比较
    //以2，4，1举例
    //默认最小值为2
    //第一轮：2-2=0 最小值=2
    //第二轮：4-2=2 最小值=2
    //第三轮：1-2=-1 最小值=1(此时最小值已经改变了，因为1<2)
    //没了
    //问题：假设数组改为2，4，1，4，最小值和最大值分别会是多少？

    /**
     * dp思想(dynamic programming)
     * 动态计算最小值
     *
     * 执行结果：通过
     * 执行用时：2 ms, 在所有 Java 提交中击败了93.52%的用户
     * 内存消耗：51.4 MB, 在所有 Java 提交中击败了39.99%的用户
     * */
    public int maxProfit(int[] prices) {
        int min = prices[0];
        int max = Integer.MIN_VALUE;
        for (int i = 0; i < prices.length; i++) {
            if (prices[i] - min > max) {
                max = prices[i] - min;
            }
            min = Math.min(min, prices[i]);
        }
        return Math.max(0, max);
    }

}
