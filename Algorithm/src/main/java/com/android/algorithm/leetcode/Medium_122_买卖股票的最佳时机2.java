package com.android.algorithm.leetcode;

import org.junit.Test;

public class Medium_122_买卖股票的最佳时机2 {

    /**
     * 给定一个数组 prices ，其中 prices[i] 是一支给定股票第 i 天的价格。
     * <p>
     * 设计一个算法来计算你所能获取的最大利润。你可以尽可能地完成更多的交易（多次买卖一支股票）。
     * <p>
     * 注意：你不能同时参与多笔交易（你必须在再次购买前出售掉之前的股票）。
     * <p>
     * 来源：力扣（LeetCode）
     * 链接：https://leetcode-cn.com/problems/best-time-to-buy-and-sell-stock-ii
     * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
     */

    @Test
    public void main() {
        int[] prices = new int[]{1, 2, 3, 4, 5};
        System.out.println(maxProfit(prices));
    }

    /**
     * 贪心，只记录正利润
     * 比如，1，2，3，4，5，最大利润是(5-4)+(4-3)+(3-2)+(2-1)=4
     * 当利润是负值时就不要了
     *
     * 执行结果：通过
     * 执行用时：1 ms, 在所有 Java 提交中击败了99.61%的用户
     * 内存消耗：38.1 MB, 在所有 Java 提交中击败了65.93%的用户
     */
    public int maxProfit(int[] prices) {
        int res = 0;
        if (prices == null || prices.length < 2) return res;
        for (int i = 1; i < prices.length; i++) {
            res += Math.max(prices[i] - prices[i - 1], 0);
        }
        return res;
    }

}
