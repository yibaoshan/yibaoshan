package com.android.algorithm.leetcode;

import org.junit.Test;

public class Medium_134_加油站 {

    /**
     * 在一条环路上有 N 个加油站，其中第 i 个加油站有汽油 gas[i] 升。
     * <p>
     * 你有一辆油箱容量无限的的汽车，从第 i 个加油站开往第 i+1 个加油站需要消耗汽油 cost[i] 升。你从其中的一个加油站出发，开始时油箱为空。
     * <p>
     * 如果你可以绕环路行驶一周，则返回出发时加油站的编号，否则返回 -1。
     * <p>
     * <p>
     * 来源：力扣（LeetCode）
     * 链接：https://leetcode-cn.com/problems/gas-station
     * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
     */

    @Test
    public void main() {
        int[] gas = new int[]{0, 0, 0, 0, 0, 2};
        int[] cost = new int[]{0, 0, 0, 0, 1, 0};
        System.out.println(canCompleteCircuit(gas, cost));
    }

    /**
     * 暴力循环
     * 执行结果：通过
     * 执行用时：59 ms, 在所有 Java 提交中击败了8.95%的用户
     * 内存消耗：58.9 MB, 在所有 Java 提交中击败了73.35%的用户
     */
    public int canCompleteCircuit(int[] gas, int[] cost) {
        int index = 0;
        while (index < gas.length) {
            if (gas[index] == 0 && cost[index] == 0) {
                index++;
                continue;
            }
            int sum = 0;
            for (int i = index; i < gas.length; i++) {
                sum += gas[i] - cost[i];
                if (sum < 0) break;
            }
            if (sum >= 0) {
                for (int i = 0; i < index; i++) {
                    sum += gas[i] - cost[i];
                    if (sum < 0) break;
                }
                if (sum >= 0) return index;
            }
            index++;
        }
        return -1;
    }

}
