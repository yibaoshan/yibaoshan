package com.android.algorithm.leetcode;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Medium_986_区间列表的交集 {

    /**
     * 给定两个由一些 闭区间 组成的列表，firstList 和 secondList ，其中 firstList[i] = [starti, endi] 而 secondList[j] = [startj, endj] 。每个区间列表都是成对 不相交 的，并且 已经排序 。
     * <p>
     * 返回这 两个区间列表的交集 。
     * <p>
     * 形式上，闭区间 [a, b]（其中 a <= b）表示实数 x 的集合，而 a <= x <= b 。
     * <p>
     * 两个闭区间的 交集 是一组实数，要么为空集，要么为闭区间。例如，[1, 3] 和 [2, 4] 的交集为 [2, 3] 。
     * <p>
     * 来源：力扣（LeetCode）
     * 链接：https://leetcode-cn.com/problems/interval-list-intersections
     * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
     */

    @Test
    public void main() {
        int[][] firstList = new int[][]{new int[]{1, 7}};
        int[][] secondList = new int[][]{new int[]{3, 10}};
        int[][] res = intervalIntersection(firstList, secondList);
        for (int[] ints : res) System.out.println(Arrays.toString(ints));
    }

    /**
     * 双指针解法，思路：由于两个区间数组都排好序了
     * 执行结果：通过
     * 执行用时：3 ms, 在所有 Java 提交中击败了96.99%的用户
     * 内存消耗：39.1 MB, 在所有 Java 提交中击败了75.32%的用户
     */
    public int[][] intervalIntersection(int[][] firstList, int[][] secondList) {
        if (firstList == null || secondList == null || firstList.length == 0 || secondList.length == 0)
            return new int[][]{};
        List<int[]> res = new ArrayList<>();
        int firstIndex = 0, secondIndex = 0;
        while (firstIndex < firstList.length && secondIndex < secondList.length) {
            int[] firstArray = firstList[firstIndex];
            int[] secondArray = secondList[secondIndex];

            int leftValue = Math.max(firstArray[0], secondArray[0]);
            int rightValue = Math.min(firstArray[1], secondArray[1]);

            if (leftValue <= rightValue) res.add(new int[]{leftValue, rightValue});

            if (firstArray[1] < secondArray[1]) firstIndex++;
            else secondIndex++;
        }
        return res.toArray(new int[res.size()][]);
    }

}
