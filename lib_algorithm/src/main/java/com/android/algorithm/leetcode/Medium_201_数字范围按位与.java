package com.android.algorithm.leetcode;

import org.junit.Test;

public class Medium_201_数字范围按位与 {

    /**
     * 给你两个整数 left 和 right ，表示区间 [left, right] ，返回此区间内所有数字 按位与 的结果（包含 left 、right 端点）。
     * <p>
     * <p>
     * 来源：力扣（LeetCode）
     * 链接：https://leetcode-cn.com/problems/bitwise-and-of-numbers-range
     * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
     */

    @Test
    public void main() {
        int left = 5;
        int right = 7;
        System.out.println(rangeBitwiseAnd(left, right));
    }

    /**
     * 执行结果：通过
     * 执行用时：3 ms, 在所有 Java 提交中击败了100.00%的用户
     * 内存消耗：38 MB, 在所有 Java 提交中击败了8.79%的用户
     * */
    public int rangeBitwiseAnd(int left, int right) {
        if (left == right) {
            return left;
        }
        int temp = ~(right ^ left);
        temp &= temp >> 1;
        temp &= temp >> 2;
        temp &= temp >> 4;
        temp &= temp >> 8;
        temp &= temp >> 16;
        return left & temp;
    }

}
