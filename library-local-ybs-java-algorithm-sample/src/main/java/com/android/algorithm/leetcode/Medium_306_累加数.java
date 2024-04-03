package com.android.algorithm.leetcode;

import org.junit.Test;

public class Medium_306_累加数 {

    /**
     * 累加数 是一个字符串，组成它的数字可以形成累加序列。
     * <p>
     * 一个有效的 累加序列 必须 至少 包含 3 个数。除了最开始的两个数以外，字符串中的其他数都等于它之前两个数相加的和。
     * <p>
     * 给你一个只包含数字 '0'-'9' 的字符串，编写一个算法来判断给定输入是否是 累加数 。如果是，返回 true ；否则，返回 false 。
     * <p>
     * 说明：累加序列里的数 不会 以 0 开头，所以不会出现 1, 2, 03 或者 1, 02, 3 的情况。
     * <p>
     * <p>
     * 来源：力扣（LeetCode）
     * 链接：https://leetcode-cn.com/problems/additive-number
     * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
     */

    @Test
    public void main() {

    }

    public boolean isAdditiveNumber(String num) {
        return backtrack(num, 0, 0, 0, 0);
    }

    /**
     * 评论区答案
     * 执行结果：通过
     * 执行用时：0 ms, 在所有 Java 提交中击败了100.00%的用户
     * 内存消耗：36.1 MB, 在所有 Java 提交中击败了80.31%的用户
     */
    private boolean backtrack(String num, int index, int count, long prevprev, long prev) {
        if (index >= num.length()) return count > 2;
        long cur = 0;
        for (int i = index; i < num.length(); i++) {
            char c = num.charAt(i);
            if (num.charAt(index) == '0' && i > index) {
                // 剪枝1：不能做为前导0，但是它自己是可以单独做为0来使用的
                return false;
            }
            cur = cur * 10 + c - '0';
            if (count >= 2) {
                long sum = prevprev + prev;
                if (cur > sum) {
                    // 剪枝2：如果当前数比之前两数的和大了，说明不合适
                    return false;
                }
                if (cur < sum) {
                    // 剪枝3：如果当前数比之前两数的和小了，说明还不够，可以继续添加新的字符进来
                    continue;
                }
            }
            // 当前满足条件了，或者还不到两个数，向下一层探索
            if (backtrack(num, i + 1, count + 1, prev, cur)) {
                return true;
            }
        }
        return false;
    }

}
