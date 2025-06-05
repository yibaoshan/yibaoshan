package com.android.algorithm.leetcode;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Stack;

public class Medium_56_合并区间 {

    @Test
    public void main() {
        int[][] ints = new int[][]{
                new int[]{2,3},
                new int[]{2,2},
                new int[]{3,3},
                new int[]{1,3},
                new int[]{5,7},
                new int[]{2,2},
                new int[]{4,6},
        };
        for (int i = 0; i < ints.length; i++) {
            System.out.print(Arrays.toString(ints[i]) + " ");
        }
        quickSort(ints, 0, ints.length - 1);
        System.out.println();
        for (int i = 0; i < ints.length; i++) {
            System.out.print(Arrays.toString(ints[i]) + " ");
        }
        int[][] res = merge(ints);
        System.out.println();
        for (int i = 0; i < res.length; i++) {
            System.out.print(Arrays.toString(res[i]) + " ");
        }
    }

    /**
     * 执行用时分布11ms击败6.68%
     * 消耗内存分布45.86MB击败26.33%
     * */
    public int[][] merge(int[][] intervals) {
        if (intervals == null || intervals.length == 0) return null;
        Arrays.sort(intervals, Comparator.comparingInt(o -> o[0]));
        Stack<int[]> queue = new Stack<>();
        queue.add(intervals[0]);

        for (int i = 1; i < intervals.length; i++) {
            int[] last = queue.peek();
            int[] cur = intervals[i];
            if (hasInterval(cur, last)) {
                queue.pop();
                queue.add(new int[]{Math.min(cur[0], last[0]), Math.max(cur[1], last[1])});
            } else queue.add(cur);
        }

        return queue.toArray(new int[1][queue.size()]);
    }

    private boolean hasInterval(int[] nums1, int[] nums2) {
        return nums1[0] <= nums2[1] && nums1[1] >= nums2[0];
    }

    /**
     * 双指针快排，时间复杂度0(n*log(n))
     * 执行结果：通过
     * 执行用时：5 ms, 在所有 Java 提交中击败了96.54%的用户
     * 内存消耗：40.5 MB, 在所有 Java 提交中击败了94.79%的用户
     */
    public int[][] merge2(int[][] intervals) {
        if (intervals == null || intervals.length == 1) return intervals;
        quickSort(intervals, 0, intervals.length - 1);
        List<int[]> list = new ArrayList<>();
        for (int i = 0; i < intervals.length; i++) {
            if (list.size() == 0) {
                list.add(intervals[i]);
                continue;
            }
            int[] last = list.get(list.size() - 1);
            int[] cur = intervals[i];
            if (cur[0] > last[1]) {
                list.add(cur);
                continue;
            }
            list.remove(list.size() - 1);
            list.add(new int[]{Math.min(cur[0], last[0]), Math.max(cur[1], last[1])});
        }
        return list.toArray(new int[list.size()][]);
    }

    private void quickSort(int[][] array, int begin, int end) {
        if (begin >= end) return;
        int low = begin, high = end;
        int[] cur = array[low];
        boolean direction = true;
        out:
        while (low < high) {
            if (direction) {
                for (int i = high; i >= low; i--) {
                    if (array[i][0] < cur[0]) {
                        array[low] = array[i];
                        low++;
                        high = i;
                        direction = !direction;
                        continue out;
                    }
                }
                high = low;
            } else {
                for (int i = low; i <= high; i++) {
                    if (array[i][0] > cur[0]) {
                        array[high] = array[i];
                        high--;
                        low = i;
                        direction = !direction;
                        continue out;
                    }
                }
                low = high;
            }
        }
        array[low] = cur;
        quickSort(array, begin, low - 1);
        quickSort(array, low + 1, end);
    }

}
