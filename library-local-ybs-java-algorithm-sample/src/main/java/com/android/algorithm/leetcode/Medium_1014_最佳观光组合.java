package com.android.algorithm.leetcode;

import org.junit.Test;

import static java.lang.Math.max;

public class Medium_1014_最佳观光组合 {

    /**
     * 给你一个正整数数组 values，其中 values[i] 表示第 i 个观光景点的评分，并且两个景点 i 和 j 之间的 距离 为 j - i。
     * <p>
     * 一对景点（i < j）组成的观光组合的得分为 values[i] + values[j] + i - j ，也就是景点的评分之和 减去 它们两者之间的距离。
     * <p>
     * 返回一对观光景点能取得的最高分。
     * <p>
     * 示例 1：
     * <p>
     * 输入：values = [8,1,5,2,6]
     * 输出：11
     * 解释：i = 0, j = 2, values[i] + values[j] + i - j = 8 + 5 + 0 - 2 = 11
     * 示例 2：
     * <p>
     * 输入：values = [1,2]
     * 输出：2
     *  
     * 来源：力扣（LeetCode）
     * 链接：https://leetcode-cn.com/problems/best-sightseeing-pair
     * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
     */

    @Test
    public void main() {
        int[] values = new int[]{8, 1, 5, 2, 6};
//        int[] values = new int[]{1, 2};
        System.out.println(maxScoreSightseeingPair(values));
        System.out.println(maxScoreSightseeingPair3(values));
    }

    /**
     * 评论区答案
     * 考虑一次遍历，观光组合的得分为 values[i] + values[j] + i - j，
     * <p>
     * 由此可以推算出，当遍历到j时，只需要知道0到j-1区间最大值，该值即为j下标的最大值。
     */

    public int maxScoreSightseeingPair(int[] values) {
        int preMax = values[0];
        int res = 0;
        for (int i = 1; i < values.length; i++) {
            preMax = max(values[i - 1] + i - 1, preMax);
            res = max(res, preMax + values[i] - i);
        }
        return res;
    }

    /**
     * 超时
     */
    public int maxScoreSightseeingPair2(int[] values) {
        int max = Integer.MIN_VALUE;
        for (int i = 0; i < values.length; i++) {
            for (int j = 0; j < values.length; j++) {
                if (i == j) continue;
                int distance = Math.abs(i - j);
                max = max(values[i] + values[j] - distance, max);
            }
        }
        return max;
    }

    /**
     * 评论区答案
     * 思路其实很简单，需要一点点对数学归纳法的理解： 假设我们已知前一个节点 j 能组成的最大的组合为（i，j），
     * 那么紧接着的一个节点 j+1 最大得分的组合一定是（i，j+1）和（j，j+1）这两个组合中较大的一个。
     * 可以简单证明一下为什么 j+1 与 j 之前的节点组合的话，最大的一定是（i，j+1）：
     * <p>
     * 记组合(i, j)的得分为f(i, j)
     * 对 j 之前的任意节点 k（即0<= k < j）, 有这样一个关系：
     * f(k, j+1) = f(k, j) - values[j] + values[j+1] - 1
     * 可见,f(k, j)之后的部分是一个定值，也就是说f(k, j+1)的大小仅和f(k, j)的得分相关
     * 从假设中我们可知，当 k 取 i 时, f(k, j)取到最大值
     * 所以当 k 取 i 时f(k, j+1)也取到最大值
     * <p>
     * 有这个递推关系，也找到了本问题的最佳子结构，写出一个简单的动态规划解法也就是轻而易举的了
     */
    public int maxScoreSightseeingPair3(int[] values) {
        int score = values[0] + values[1] - 1;
        int res = score;
        for (int i = 2; i < values.length; i++) {
            score = Math.max(values[i] + values[i - 1] - 1, score - values[i - 1] + values[i] - 1);
            res = Math.max(res, score);
        }
        return res;
    }

}
