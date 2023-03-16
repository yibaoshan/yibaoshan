package com.android.algorithm.leetcode;

import org.junit.Test;

import java.util.Arrays;

public class Medium_435_无重叠区间 {

    /**
     * 给定一个区间的集合，找到需要移除区间的最小数量，使剩余区间互不重叠。
     * <p>
     * 注意:
     * <p>
     * 可以认为区间的终点总是大于它的起点。
     * 区间 [1,2] 和 [2,3] 的边界相互“接触”，但没有相互重叠。
     * <p>
     * 来源：力扣（LeetCode）
     * 链接：https://leetcode-cn.com/problems/non-overlapping-intervals
     * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
     */

    @Test
    public void main() {
        //[[-52,31],[-73,-26],[82,97],[-65,-11],[-62,-49],[95,99],[58,95],[-31,49],[66,98],[-63,2],[30,47],[-40,-26]]
        int[][] intervals = new int[][]{
                new int[]{-52, 31},
                new int[]{-73, -26},
                new int[]{82, 97},
                new int[]{-65, -11},
                new int[]{-62, -49},
                new int[]{95, 99},
                new int[]{58, 95},
                new int[]{-31, 49},
                new int[]{66, 98},
                new int[]{-63, 2},
                new int[]{30, 47},
                new int[]{-40, -26}
        };
        for (int[] array : intervals) System.out.print(Arrays.toString(array));
        quickSort(intervals, 0, intervals.length - 1);
        System.out.println();
        for (int[] array : intervals) System.out.print(Arrays.toString(array));
        System.out.println();
        System.out.println(eraseOverlapIntervals2(intervals));
    }

    /**
     * 贪心算法
     * 执行结果：通过
     * 执行用时：150 ms, 在所有 Java 提交中击败了5.04%的用户
     * 内存消耗：95.2 MB, 在所有 Java 提交中击败了5.14%的用户
     */
    public int eraseOverlapIntervals2(int[][] intervals) {
        quickSort(intervals, 0, intervals.length - 1);
        int cnt = 1;
        int len = intervals.length;
        int end = intervals[0][1];
        for (int i = 1; i < len; i++) {
            if (end <= intervals[i][0]) {
                cnt++;
                end = intervals[i][1];
            }
        }
        return len - cnt;
    }

    public int eraseOverlapIntervals(int[][] intervals) {
        if (intervals == null || intervals.length == 0) return 0;
        quickSort(intervals, 0, intervals.length - 1);
        int last = intervals[0][1];
        int count = 0;
        int end = intervals[0][1];
        for (int[] interval : intervals) {
            if (interval[0] >= end) {
                count++;             // 找到下一个区间了
                end = interval[1];
            }
        }
        return intervals.length - count;
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
                    if (array[i][1] < cur[1]) {
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
                    if (array[i][1] > cur[1]) {
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
