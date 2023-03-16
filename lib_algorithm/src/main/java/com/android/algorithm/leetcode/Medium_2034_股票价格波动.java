package com.android.algorithm.leetcode;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class Medium_2034_股票价格波动 {

    /**
     * 给你一支股票价格的数据流。数据流中每一条记录包含一个 时间戳 和该时间点股票对应的 价格 。
     * <p>
     * 不巧的是，由于股票市场内在的波动性，股票价格记录可能不是按时间顺序到来的。某些情况下，有的记录可能是错的。如果两个有相同时间戳的记录出现在数据流中，前一条记录视为错误记录，后出现的记录 更正 前一条错误的记录。
     * <p>
     * 请你设计一个算法，实现：
     * <p>
     * 更新 股票在某一时间戳的股票价格，如果有之前同一时间戳的价格，这一操作将 更正 之前的错误价格。
     * 找到当前记录里 最新股票价格 。最新股票价格 定义为时间戳最晚的股票价格。
     * 找到当前记录里股票的 最高价格 。
     * 找到当前记录里股票的 最低价格 。
     * <p>
     * 来源：力扣（LeetCode）
     * 链接：https://leetcode-cn.com/problems/stock-price-fluctuation
     * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
     */

    @Test
    public void main() {

    }

    class StockPrice {

        int cur;
        Map<Integer, Integer> map = new HashMap<>();
        TreeMap<Integer, Integer> ts = new TreeMap<>();

        public void update(int timestamp, int price) {
            cur = Math.max(cur, timestamp);
            if (map.containsKey(timestamp)) {
                int old = map.get(timestamp);
                int cnt = ts.get(old);
                if (cnt == 1) ts.remove(old);
                else ts.put(old, cnt - 1);
            }
            map.put(timestamp, price);
            ts.put(price, ts.getOrDefault(price, 0) + 1);
        }

        public int current() {
            return map.get(cur);
        }

        public int maximum() {
            return ts.lastKey();
        }

        public int minimum() {
            return ts.firstKey();
        }
    }


}
